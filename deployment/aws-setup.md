# SHARKNO - AWS Production Deployment

## 📋 Prerequisites

1. **AWS Account** with appropriate permissions
2. **Domain name** (e.g., sharkno.com)
3. **GitHub repository** with SHARKNO code
4. **AWS CLI** installed and configured

## 🏗️ Step 1: AWS CLI Setup

```bash
# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Configure AWS CLI
aws configure
# AWS Access Key ID: <your-access-key>
# AWS Secret Access Key: <your-secret-key>
# Default region: us-west-2
# Default output format: json
```

## ☸️ Step 2: EKS Cluster Creation

```bash
# Install eksctl
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin

# Create EKS cluster
eksctl create cluster \
    --name sharkno-production \
    --region us-west-2 \
    --nodegroup-name sharkno-workers \
    --node-type m5.large \
    --nodes 3 \
    --nodes-min 2 \
    --nodes-max 8 \
    --managed \
    --ssh-access \
    --ssh-public-key <your-key-name>

# Update kubeconfig
aws eks update-kubeconfig --region us-west-2 --name sharkno-production
```

## 🌐 Step 3: Application Load Balancer Setup

```bash
# Install AWS Load Balancer Controller
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller/crds?ref=master"

# Create IAM role for service account
eksctl create iamserviceaccount \
  --cluster=sharkno-production \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::aws:policy/ElasticLoadBalancingFullAccess \
  --override-existing-serviceaccounts \
  --approve

# Install controller via Helm
helm repo add eks https://aws.github.io/eks-charts
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=sharkno-production \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller
```

## 🗄️ Step 4: RDS MongoDB/DocumentDB Setup

```bash
# Create DocumentDB cluster (MongoDB-compatible)
aws docdb create-db-cluster \
    --db-cluster-identifier sharkno-docdb \
    --engine docdb \
    --master-username sharkno \
    --master-user-password <secure-password> \
    --vpc-security-group-ids <security-group-id> \
    --db-subnet-group-name <subnet-group-name>

# Create instances
aws docdb create-db-instance \
    --db-instance-identifier sharkno-docdb-instance \
    --db-instance-class db.t3.medium \
    --engine docdb \
    --db-cluster-identifier sharkno-docdb
```

## 📦 Step 5: ECR Repository Setup

```bash
# Create ECR repositories
aws ecr create-repository --repository-name sharkno/backend --region us-west-2
aws ecr create-repository --repository-name sharkno/frontend --region us-west-2

# Get login token
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-west-2.amazonaws.com

# Build and push images
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export ECR_REGISTRY=${AWS_ACCOUNT_ID}.dkr.ecr.us-west-2.amazonaws.com

docker build -t $ECR_REGISTRY/sharkno/backend:latest -f Dockerfile.backend .
docker push $ECR_REGISTRY/sharkno/backend:latest

docker build -t $ECR_REGISTRY/sharkno/frontend:latest -f Dockerfile.frontend .
docker push $ECR_REGISTRY/sharkno/frontend:latest
```

## 🔐 Step 6: AWS Secrets Manager

```bash
# Create secrets in AWS Secrets Manager
aws secretsmanager create-secret \
    --name "sharkno/production/database" \
    --description "SHARKNO database connection string" \
    --secret-string '{"MONGO_URL":"mongodb://sharkno:<password>@sharkno-docdb.cluster-xyz.us-west-2.docdb.amazonaws.com:27017/sharkno?ssl=true&replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false"}'

aws secretsmanager create-secret \
    --name "sharkno/production/jwt" \
    --description "SHARKNO JWT secret" \
    --secret-string '{"JWT_SECRET":"<your-secure-jwt-secret>"}'

aws secretsmanager create-secret \
    --name "sharkno/production/linkedin" \
    --description "SHARKNO LinkedIn API credentials" \
    --secret-string '{"LINKEDIN_CLIENT_ID":"77hdmkkp0rmtof","LINKEDIN_CLIENT_SECRET":"WPL_AP1.sFykT88SDCYZEPbK.ofU6qA=="}'
```

## 📋 Step 7: Update Kubernetes Manifests for AWS

Create AWS-specific deployment files:

```yaml
# k8s/aws/backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sharkno-backend
  namespace: sharkno
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sharkno-backend
  template:
    metadata:
      labels:
        app: sharkno-backend
    spec:
      serviceAccountName: sharkno-backend-sa
      containers:
      - name: sharkno-backend
        image: <AWS_ACCOUNT_ID>.dkr.ecr.us-west-2.amazonaws.com/sharkno/backend:latest
        ports:
        - containerPort: 8001
        env:
        - name: MONGO_URL
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: MONGO_URL
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: JWT_SECRET
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"

---
apiVersion: v1
kind: Service
metadata:
  name: sharkno-backend
  namespace: sharkno
spec:
  selector:
    app: sharkno-backend
  ports:
  - port: 8001
    targetPort: 8001
  type: ClusterIP
```

## 🌐 Step 8: ALB Ingress Configuration

```yaml
# k8s/aws/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: sharkno-ingress
  namespace: sharkno
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP": 80}, {"HTTPS":443}]'
    alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:us-west-2:<account>:certificate/<cert-id>
    alb.ingress.kubernetes.io/ssl-redirect: '443'
spec:
  rules:
  - host: app.sharkno.com
    http:
      paths:
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: sharkno-backend
            port:
              number: 8001
      - path: /
        pathType: Prefix
        backend:
          service:
            name: sharkno-frontend
            port:
              number: 80
```

## 🚀 Step 9: Deploy to AWS

```bash
# Apply manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/aws/secrets.yaml
kubectl apply -f k8s/aws/backend-deployment.yaml
kubectl apply -f k8s/aws/frontend-deployment.yaml
kubectl apply -f k8s/aws/ingress.yaml

# Wait for deployment
kubectl rollout status deployment/sharkno-backend -n sharkno
kubectl rollout status deployment/sharkno-frontend -n sharkno
```

## 📊 Step 10: CloudWatch Monitoring

```bash
# Install CloudWatch Container Insights
kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/cloudwatch-namespace.yaml

kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/cwagent/cwagent-daemonset.yaml

kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/fluentd/fluentd-daemonset-cloudwatch.yaml
```

## 💰 Cost Optimization

- Use Spot Instances for worker nodes
- Implement cluster autoscaling
- Use Fargate for specific workloads
- Monitor costs with AWS Cost Explorer
- Set up billing alerts

## 🔒 Security

- Enable VPC Flow Logs
- Configure Network ACLs
- Use IAM roles for service accounts
- Enable AWS CloudTrail
- Regular security assessments