#!/bin/bash
# SHARKNO AWS Deployment Script

set -e

# Configuration
PROJECT_NAME="sharkno"
CLUSTER_NAME="sharkno-production"
REGION="us-west-2"
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"

echo "🚀 SHARKNO AWS Deployment Started"
echo "📊 Account ID: $AWS_ACCOUNT_ID"
echo "🌐 Region: $REGION"
echo "☸️  Cluster: $CLUSTER_NAME"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check AWS CLI
    if ! command -v aws &> /dev/null; then
        print_error "AWS CLI not found. Please install it first."
        exit 1
    fi
    
    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl not found. Please install it first."
        exit 1
    fi
    
    # Check eksctl
    if ! command -v eksctl &> /dev/null; then
        print_error "eksctl not found. Please install it first."
        exit 1
    fi
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker not found. Please install it first."
        exit 1
    fi
    
    # Check AWS credentials
    if ! aws sts get-caller-identity &> /dev/null; then
        print_error "AWS credentials not configured. Run 'aws configure' first."
        exit 1
    fi
    
    print_success "All prerequisites met!"
}

# Create EKS cluster if it doesn't exist
create_cluster() {
    print_status "Checking if EKS cluster exists..."
    
    if eksctl get cluster --name $CLUSTER_NAME --region $REGION &> /dev/null; then
        print_warning "Cluster $CLUSTER_NAME already exists. Skipping creation."
    else
        print_status "Creating EKS cluster $CLUSTER_NAME..."
        
        eksctl create cluster \
            --name $CLUSTER_NAME \
            --region $REGION \
            --nodegroup-name ${PROJECT_NAME}-workers \
            --node-type m5.large \
            --nodes 3 \
            --nodes-min 2 \
            --nodes-max 8 \
            --managed \
            --with-oidc \
            --ssh-access=false \
            --tags Environment=production,Application=sharkno
        
        print_success "EKS cluster created successfully!"
    fi
    
    # Update kubeconfig
    aws eks update-kubeconfig --region $REGION --name $CLUSTER_NAME
    print_success "Kubeconfig updated!"
}

# Setup ECR repositories
setup_ecr() {
    print_status "Setting up ECR repositories..."
    
    # Create backend repository
    aws ecr describe-repositories --repository-names ${PROJECT_NAME}/backend --region $REGION &> /dev/null || \
    aws ecr create-repository --repository-name ${PROJECT_NAME}/backend --region $REGION
    
    # Create frontend repository
    aws ecr describe-repositories --repository-names ${PROJECT_NAME}/frontend --region $REGION &> /dev/null || \
    aws ecr create-repository --repository-name ${PROJECT_NAME}/frontend --region $REGION
    
    print_success "ECR repositories ready!"
}

# Build and push Docker images
build_and_push() {
    print_status "Building and pushing Docker images..."
    
    # Login to ECR
    aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_REGISTRY
    
    # Build backend
    print_status "Building backend image..."
    docker build -t $ECR_REGISTRY/${PROJECT_NAME}/backend:latest -f Dockerfile.backend .
    docker push $ECR_REGISTRY/${PROJECT_NAME}/backend:latest
    print_success "Backend image pushed!"
    
    # Build frontend
    print_status "Building frontend image..."
    docker build -t $ECR_REGISTRY/${PROJECT_NAME}/frontend:latest -f Dockerfile.frontend .
    docker push $ECR_REGISTRY/${PROJECT_NAME}/frontend:latest
    print_success "Frontend image pushed!"
}

# Install AWS Load Balancer Controller
install_alb_controller() {
    print_status "Installing AWS Load Balancer Controller..."
    
    # Check if controller exists
    if kubectl get deployment aws-load-balancer-controller -n kube-system &> /dev/null; then
        print_warning "AWS Load Balancer Controller already exists. Skipping installation."
        return
    fi
    
    # Create IAM role for service account
    eksctl create iamserviceaccount \
        --cluster=$CLUSTER_NAME \
        --namespace=kube-system \
        --name=aws-load-balancer-controller \
        --role-name=AmazonEKSLoadBalancerControllerRole \
        --attach-policy-arn=arn:aws:iam::aws:policy/ElasticLoadBalancingFullAccess \
        --approve \
        --override-existing-serviceaccounts \
        --region=$REGION
    
    # Install controller via Helm
    helm repo add eks https://aws.github.io/eks-charts
    helm repo update
    
    helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
        -n kube-system \
        --set clusterName=$CLUSTER_NAME \
        --set serviceAccount.create=false \
        --set serviceAccount.name=aws-load-balancer-controller
    
    print_success "AWS Load Balancer Controller installed!"
}

# Deploy application
deploy_application() {
    print_status "Deploying SHARKNO application..."
    
    # Create namespace
    kubectl apply -f k8s/namespace.yaml
    
    # Update deployment files with correct image URLs
    sed -i.bak "s|123456789012|$AWS_ACCOUNT_ID|g" k8s/aws/backend-deployment.yaml
    sed -i.bak "s|123456789012|$AWS_ACCOUNT_ID|g" k8s/aws/frontend-deployment.yaml
    sed -i.bak "s|ACCOUNT_ID|$AWS_ACCOUNT_ID|g" k8s/aws/ingress.yaml
    sed -i.bak "s|ACCOUNT_ID|$AWS_ACCOUNT_ID|g" k8s/aws/rbac.yaml
    
    # Apply RBAC
    kubectl apply -f k8s/aws/rbac.yaml
    
    # Apply deployments
    kubectl apply -f k8s/aws/backend-deployment.yaml
    kubectl apply -f k8s/aws/frontend-deployment.yaml
    
    # Apply ingress
    kubectl apply -f k8s/aws/ingress.yaml
    
    # Wait for deployments
    print_status "Waiting for deployments to be ready..."
    kubectl rollout status deployment/sharkno-backend -n sharkno --timeout=300s
    kubectl rollout status deployment/sharkno-frontend -n sharkno --timeout=300s
    
    print_success "Application deployed successfully!"
}

# Verify deployment
verify_deployment() {
    print_status "Verifying deployment..."
    
    echo ""
    echo "📊 Cluster Status:"
    kubectl get nodes
    
    echo ""
    echo "🚀 SHARKNO Pods:"
    kubectl get pods -n sharkno
    
    echo ""
    echo "🌐 Services:"
    kubectl get services -n sharkno
    
    echo ""
    echo "🔗 Ingress:"
    kubectl get ingress -n sharkno
    
    echo ""
    echo "📈 Load Balancer:"
    ALB_ADDRESS=$(kubectl get ingress sharkno-ingress -n sharkno -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
    if [ ! -z "$ALB_ADDRESS" ]; then
        echo "Load Balancer Address: $ALB_ADDRESS"
        echo ""
        echo "🎉 SHARKNO is accessible at:"
        echo "   Frontend: https://app.sharkno.com (after DNS configuration)"
        echo "   API: https://api.sharkno.com (after DNS configuration)"
        echo "   Load Balancer: https://$ALB_ADDRESS"
    fi
}

# Main deployment function
main() {
    echo "🌾 SHARKNO Agricultural Network - AWS Deployment"
    echo "==============================================="
    
    check_prerequisites
    create_cluster
    setup_ecr
    build_and_push
    install_alb_controller
    deploy_application
    verify_deployment
    
    echo ""
    print_success "🎉 SHARKNO deployment completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Configure DNS records to point to the Load Balancer"
    echo "2. Update SSL certificate ARN in ingress.yaml"
    echo "3. Create Kubernetes secrets with production credentials"
    echo "4. Test the application functionality"
    echo ""
    echo "For detailed instructions, see: deployment/aws-setup.md"
}

# Check if script is run with arguments
if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
    echo "SHARKNO AWS Deployment Script"
    echo ""
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  --help, -h     Show this help message"
    echo "  --check        Check prerequisites only"
    echo "  --cluster      Create cluster only"
    echo "  --deploy       Deploy application only (assumes cluster exists)"
    echo ""
    exit 0
elif [ "$1" == "--check" ]; then
    check_prerequisites
elif [ "$1" == "--cluster" ]; then
    check_prerequisites
    create_cluster
    install_alb_controller
elif [ "$1" == "--deploy" ]; then
    check_prerequisites
    setup_ecr
    build_and_push
    deploy_application
    verify_deployment
else
    main
fi