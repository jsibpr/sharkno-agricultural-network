# SHARKNO - Google Cloud Production Deployment

## 📋 Prerequisites

1. **Google Cloud Account** with billing enabled
2. **Domain name** (e.g., sharkno.com)
3. **GitHub repository** with SHARKNO code
4. **MongoDB Atlas account** (recommended)

## 🏗️ Step 1: Google Cloud Project Setup

```bash
# Install Google Cloud CLI
curl https://sdk.cloud.google.com | bash
exec -l $SHELL
gcloud init

# Create new project
export PROJECT_ID="sharkno-agricultural-prod"
gcloud projects create $PROJECT_ID
gcloud config set project $PROJECT_ID

# Enable required APIs
gcloud services enable container.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable cloudbuild.googleapis.com
gcloud services enable compute.googleapis.com
gcloud services enable dns.googleapis.com
```

## ☸️ Step 2: GKE Cluster Creation

```bash
# Create GKE cluster
gcloud container clusters create sharkno-production \
    --zone us-central1-a \
    --num-nodes 3 \
    --machine-type e2-medium \
    --enable-autoscaling \
    --min-nodes 2 \
    --max-nodes 10 \
    --enable-autorepair \
    --enable-autoupgrade \
    --disk-size 50GB \
    --disk-type pd-ssd

# Get cluster credentials
gcloud container clusters get-credentials sharkno-production --zone us-central1-a
```

## 🌐 Step 3: DNS and Static IP

```bash
# Reserve static IP
gcloud compute addresses create sharkno-ip --global

# Get the IP address
gcloud compute addresses describe sharkno-ip --global
# Note: Point your domain A records to this IP

# Create DNS zone (optional - if managing DNS through Google Cloud)
gcloud dns managed-zones create sharkno-zone \
    --dns-name=sharkno.com \
    --description="SHARKNO Agricultural Network DNS Zone"
```

## 🔐 Step 4: Service Account Setup

```bash
# Create service account for CI/CD
gcloud iam service-accounts create sharkno-cicd \
    --display-name="SHARKNO CI/CD Service Account"

# Grant necessary roles
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:sharkno-cicd@$PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/container.developer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:sharkno-cicd@$PROJECT_ID.iam.gserviceaccount.com" \
    --role="roles/storage.admin"

# Create and download key
gcloud iam service-accounts keys create sharkno-sa-key.json \
    --iam-account=sharkno-cicd@$PROJECT_ID.iam.gserviceaccount.com
```

## 📊 Step 5: MongoDB Atlas Setup

1. Go to [MongoDB Atlas](https://cloud.mongodb.com)
2. Create new cluster (M10+ recommended for production)
3. Configure network access (add GKE cluster IP ranges)
4. Create database user
5. Get connection string

## 🔑 Step 6: GitHub Secrets Configuration

Add these secrets to your GitHub repository (Settings → Secrets):

```
GCP_PROJECT_ID: sharkno-agricultural-prod
GCP_SA_KEY: <contents of sharkno-sa-key.json>
MONGO_URL: mongodb+srv://username:password@cluster.mongodb.net/sharkno_prod
JWT_SECRET: <generate-secure-random-string>
LINKEDIN_CLIENT_ID: 77hdmkkp0rmtof
LINKEDIN_CLIENT_SECRET: WPL_AP1.sFykT88SDCYZEPbK.ofU6qA==
SENDGRID_API_KEY: <your-sendgrid-key>
SENTRY_DSN: <your-sentry-dsn>
```

## 🚀 Step 7: Deploy to Production

```bash
# Clone your repository
git clone https://github.com/yourusername/sharkno-agricultural-network.git
cd sharkno-agricultural-network

# Deploy manually first time
kubectl apply -f k8s/namespace.yaml

# Create secrets
kubectl create secret generic sharkno-secrets \
  --from-literal=mongo-url="$MONGO_URL" \
  --from-literal=jwt-secret="$JWT_SECRET" \
  --from-literal=linkedin-client-id="$LINKEDIN_CLIENT_ID" \
  --from-literal=linkedin-client-secret="$LINKEDIN_CLIENT_SECRET" \
  --from-literal=sendgrid-api-key="$SENDGRID_API_KEY" \
  --from-literal=sentry-dsn="$SENTRY_DSN" \
  --namespace=sharkno

# Build and push images
export REGISTRY_HOSTNAME=gcr.io
docker build -t $REGISTRY_HOSTNAME/$PROJECT_ID/sharkno-backend:v1 -f Dockerfile.backend .
docker build -t $REGISTRY_HOSTNAME/$PROJECT_ID/sharkno-frontend:v1 -f Dockerfile.frontend .

docker push $REGISTRY_HOSTNAME/$PROJECT_ID/sharkno-backend:v1
docker push $REGISTRY_HOSTNAME/$PROJECT_ID/sharkno-frontend:v1

# Update deployment files
sed -i "s|gcr.io/PROJECT_ID|$REGISTRY_HOSTNAME/$PROJECT_ID|g" k8s/backend-deployment.yaml
sed -i "s|gcr.io/PROJECT_ID|$REGISTRY_HOSTNAME/$PROJECT_ID|g" k8s/frontend-deployment.yaml
sed -i "s|:latest|:v1|g" k8s/backend-deployment.yaml
sed -i "s|:latest|:v1|g" k8s/frontend-deployment.yaml

# Apply deployments
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```

## 📊 Step 8: Monitoring Setup

```bash
# Install Prometheus and Grafana
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/kube-prometheus-stack \
    --namespace monitoring \
    --create-namespace \
    --set grafana.adminPassword=admin123
```

## 🎯 Step 9: Domain Configuration

1. **DNS Records**: Point your domain to the static IP:
   ```
   A    app.sharkno.com    → <static-ip>
   A    api.sharkno.com    → <static-ip>
   ```

2. **SSL Certificate**: Will be automatically provisioned by Google Managed Certificates

## ✅ Step 10: Verification

```bash
# Check deployment status
kubectl get pods -n sharkno
kubectl get services -n sharkno
kubectl get ingress -n sharkno

# Check application
curl https://api.sharkno.com/health
curl https://app.sharkno.com
```

## 🔧 Troubleshooting

```bash
# View logs
kubectl logs -f deployment/sharkno-backend -n sharkno
kubectl logs -f deployment/sharkno-frontend -n sharkno

# Describe resources
kubectl describe ingress sharkno-ingress -n sharkno
kubectl describe pod <pod-name> -n sharkno

# Access pod shell
kubectl exec -it <pod-name> -n sharkno -- /bin/bash
```

## 💰 Cost Optimization

- Use preemptible nodes for development
- Set up cluster autoscaling
- Monitor resource usage with Google Cloud Monitoring
- Use appropriate machine types for workloads

## 🔐 Security Best Practices

- Regular security updates
- Network policies implementation
- RBAC configuration
- Secret rotation
- Backup strategies