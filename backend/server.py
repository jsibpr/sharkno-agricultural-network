from fastapi import FastAPI, APIRouter, HTTPException, Depends, UploadFile, File, Form, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import RedirectResponse
from motor.motor_asyncio import AsyncIOMotorClient
from pydantic import BaseModel, Field, EmailStr
from typing import List, Optional, Dict, Any
from datetime import datetime, timedelta
from pathlib import Path
import os
import uuid
import jwt
import bcrypt
from enum import Enum
import logging
import requests
import json
from urllib.parse import urlencode

# Load environment variables
from dotenv import load_dotenv
ROOT_DIR = Path(__file__).parent
load_dotenv(ROOT_DIR / '.env')

# MongoDB connection
mongo_url = os.environ['MONGO_URL']
client = AsyncIOMotorClient(mongo_url)
db = client[os.environ['DB_NAME']]

# JWT Configuration
JWT_SECRET = os.environ.get('JWT_SECRET', 'your-secret-key-here')
JWT_ALGORITHM = 'HS256'
JWT_EXPIRATION_HOURS = 24

# LinkedIn OAuth Configuration
LINKEDIN_CLIENT_ID = os.environ.get('LINKEDIN_CLIENT_ID', 'your-linkedin-client-id')
LINKEDIN_CLIENT_SECRET = os.environ.get('LINKEDIN_CLIENT_SECRET', 'your-linkedin-client-secret')
LINKEDIN_REDIRECT_URI = os.environ.get('LINKEDIN_REDIRECT_URI', 'http://localhost:8001/api/auth/linkedin/callback')

# Create FastAPI app
app = FastAPI(title="SharkNo Agricultural Professional Network")
api_router = APIRouter(prefix="/api")

# Security
security = HTTPBearer()

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_credentials=True,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Enums
class UserRole(str, Enum):
    ADMIN = "admin"
    FARMER = "farmer"
    CONSULTANT = "consultant"
    EQUIPMENT_DEALER = "equipment_dealer"
    VETERINARIAN = "veterinarian"
    AGRONOMIST = "agronomist"
    SUPPLIER = "supplier"

class ProfileType(str, Enum):
    INDIVIDUAL = "individual"
    BUSINESS = "business"
    ORGANIZATION = "organization"

class ServiceType(str, Enum):
    CONSULTATION = "consultation"
    EQUIPMENT_RENTAL = "equipment_rental"
    VETERINARY = "veterinary"
    AGRONOMIC_ADVICE = "agronomic_advice"
    CROP_PROTECTION = "crop_protection"
    SOIL_ANALYSIS = "soil_analysis"
    IRRIGATION = "irrigation"
    HARVESTING = "harvesting"

class ExperienceLevel(str, Enum):
    ENTRY = "entry"
    INTERMEDIATE = "intermediate"
    ADVANCED = "advanced"
    EXPERT = "expert"

class ValidationStatus(str, Enum):
    PENDING = "pending"
    APPROVED = "approved"
    REJECTED = "rejected"

# Pydantic Models
class UserBase(BaseModel):
    email: EmailStr
    name: str
    role: UserRole

class UserCreate(UserBase):
    password: str

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class User(UserBase):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    created_at: datetime = Field(default_factory=datetime.utcnow)
    is_active: bool = True
    profile_completed: bool = False

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: User

class Address(BaseModel):
    street: Optional[str] = None
    city: str
    state: str
    country: str
    postal_code: Optional[str] = None

class Skill(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    name: str
    category: str
    verified: bool = False
    verification_source: Optional[str] = None
    certificate_id: Optional[str] = None

class Experience(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    position: str
    company: str
    start_date: datetime
    end_date: Optional[datetime] = None
    description: Optional[str] = None
    location: Optional[str] = None
    still_working: bool = False

class Certification(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    name: str
    issuing_organization: str
    issue_date: datetime
    expiry_date: Optional[datetime] = None
    credential_id: Optional[str] = None
    verification_url: Optional[str] = None

# Project Experience Validation Models
class ProjectExperience(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    project_name: str
    project_type: str  # "irrigation", "crop_management", "livestock", "technology_implementation"
    description: str
    location: str
    start_date: datetime
    end_date: Optional[datetime] = None
    still_active: bool = False
    skills_demonstrated: List[str] = []
    collaborators: List[str] = []  # List of user IDs who worked on this project
    project_leader_id: Optional[str] = None
    project_results: Optional[str] = None
    created_by: str
    created_at: datetime = Field(default_factory=datetime.utcnow)

class ProjectValidationRequest(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    project_experience_id: str
    validator_id: str
    validated_user_id: str
    project_role: str  # What role the validated user played in the project
    skills_validated: List[str] = []  # Specific skills observed
    collaboration_description: str  # How they worked together
    performance_rating: int = Field(..., ge=1, le=5)
    would_work_again: bool
    validation_evidence: Optional[str] = None  # Photos, documents, links
    status: ValidationStatus = ValidationStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

# Enhanced validation with external tagging
class ExternalProfile(BaseModel):
    platform: str  # "linkedin", "email", "manual"
    platform_id: Optional[str] = None  # LinkedIn profile ID
    profile_url: Optional[str] = None  # LinkedIn profile URL
    email: Optional[str] = None
    name: str
    title: Optional[str] = None
    company: Optional[str] = None
    invited_to_sharkno: bool = False
    sharkno_user_id: Optional[str] = None  # If they eventually join

class EnhancedValidationRequest(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    # Core validation info
    skill_id: str
    validator_id: str
    description: str
    
    # Enhanced tagging system
    validated_user_id: Optional[str] = None  # For internal SHARKNO users
    external_profile: Optional[ExternalProfile] = None  # For external users
    
    # Project context
    project_experience_id: Optional[str] = None
    project_name: Optional[str] = None
    collaboration_period: Optional[str] = None
    specific_achievements: Optional[str] = None
    working_relationship: Optional[str] = None
    
    # Status and notifications
    status: ValidationStatus = ValidationStatus.PENDING
    external_invited: bool = False
    external_notification_sent: bool = False
    
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

# Keep old ValidationRequest for backward compatibility (deprecated)
class ValidationRequest(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    # Enhanced validation with project context
    project_experience_id: Optional[str] = None  # Link to specific project
    skill_id: str
    validator_id: str
    validated_user_id: str
    description: str
    
    # New project-focused fields
    project_name: Optional[str] = None
    collaboration_period: Optional[str] = None
    specific_achievements: Optional[str] = None
    working_relationship: Optional[str] = None  # "direct_supervisor", "colleague", "client", "team_member"
    
    status: ValidationStatus = ValidationStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

class ProfileBase(BaseModel):
    user_id: str
    profile_type: ProfileType
    title: str
    bio: Optional[str] = None
    phone: Optional[str] = None
    address: Optional[Address] = None
    website: Optional[str] = None
    profile_picture: Optional[str] = None
    skills: List[Skill] = []
    experience: List[Experience] = []
    certifications: List[Certification] = []
    rating: float = 0.0
    total_validations: int = 0

class Profile(ProfileBase):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

class ServiceBase(BaseModel):
    title: str
    description: str
    service_type: ServiceType
    price_min: Optional[float] = None
    price_max: Optional[float] = None
    currency: str = "USD"
    location: Optional[str] = None
    experience_level: ExperienceLevel
    skills_required: List[str] = []
    availability: Optional[str] = None

class Service(ServiceBase):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    provider_id: str
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None
    active: bool = True

class ReviewBase(BaseModel):
    reviewer_id: str
    reviewed_user_id: str
    service_id: Optional[str] = None
    rating: int = Field(..., ge=1, le=5)
    comment: Optional[str] = None

class Review(ReviewBase):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    created_at: datetime = Field(default_factory=datetime.utcnow)

# Utility functions
def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

def verify_password(password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(password.encode('utf-8'), hashed_password.encode('utf-8'))

def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(hours=JWT_EXPIRATION_HOURS)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, JWT_SECRET, algorithm=JWT_ALGORITHM)
    return encoded_jwt

async def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)):
    try:
        token = credentials.credentials
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        user_id: str = payload.get("sub")
        if user_id is None:
            raise HTTPException(status_code=401, detail="Invalid authentication credentials")
        
        user = await db.users.find_one({"id": user_id})
        if user is None:
            raise HTTPException(status_code=401, detail="User not found")
        
        return User(**user)
    except jwt.PyJWTError:
        raise HTTPException(status_code=401, detail="Invalid authentication credentials")

# Authentication endpoints
@api_router.post("/auth/register", response_model=TokenResponse)
async def register(user_data: UserCreate):
    # Check if user already exists
    existing_user = await db.users.find_one({"email": user_data.email})
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Hash password
    hashed_password = hash_password(user_data.password)
    
    # Create user
    user = User(
        email=user_data.email,
        name=user_data.name,
        role=user_data.role
    )
    
    # Save to database
    user_dict = user.dict()
    user_dict["hashed_password"] = hashed_password
    await db.users.insert_one(user_dict)
    
    # Create JWT token
    access_token = create_access_token(data={"sub": user.id})
    
    return TokenResponse(access_token=access_token, user=user)

@api_router.post("/auth/login", response_model=TokenResponse)
async def login(user_data: UserLogin):
    # Find user
    user = await db.users.find_one({"email": user_data.email})
    if not user:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    
    # Verify password
    if not verify_password(user_data.password, user["hashed_password"]):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    
    # Create JWT token
    access_token = create_access_token(data={"sub": user["id"]})
    
    user_obj = User(**user)
    return TokenResponse(access_token=access_token, user=user_obj)

@api_router.get("/auth/me", response_model=User)
async def get_current_user_info(current_user: User = Depends(get_current_user)):
    return current_user

# LinkedIn OAuth endpoints
class LinkedInProfile(BaseModel):
    linkedin_id: str
    first_name: str
    last_name: str
    email: str
    profile_picture: Optional[str] = None
    headline: Optional[str] = None
    summary: Optional[str] = None
    positions: List[Dict] = []
    connected_at: datetime = Field(default_factory=datetime.utcnow)

@api_router.get("/auth/linkedin")
async def linkedin_auth(current_user: User = Depends(get_current_user)):
    """Initiate LinkedIn OAuth flow"""
    params = {
        'response_type': 'code',
        'client_id': LINKEDIN_CLIENT_ID,
        'redirect_uri': LINKEDIN_REDIRECT_URI,
        'scope': 'r_liteprofile r_emailaddress',
        'state': current_user.id  # Use user ID as state for security
    }
    
    linkedin_auth_url = f"https://www.linkedin.com/oauth/v2/authorization?{urlencode(params)}"
    return {"auth_url": linkedin_auth_url}

@api_router.get("/auth/linkedin/callback")
async def linkedin_callback(request: Request, code: str, state: str):
    """Handle LinkedIn OAuth callback"""
    try:
        # Verify state matches a valid user
        user = await db.users.find_one({"id": state})
        if not user:
            raise HTTPException(status_code=400, detail="Invalid state parameter")
        
        # Exchange code for access token
        token_data = {
            'grant_type': 'authorization_code',
            'code': code,
            'redirect_uri': LINKEDIN_REDIRECT_URI,
            'client_id': LINKEDIN_CLIENT_ID,
            'client_secret': LINKEDIN_CLIENT_SECRET
        }
        
        token_response = requests.post(
            'https://www.linkedin.com/oauth/v2/accessToken',
            data=token_data,
            headers={'Content-Type': 'application/x-www-form-urlencoded'}
        )
        
        if token_response.status_code != 200:
            raise HTTPException(status_code=400, detail="Failed to get access token")
        
        access_token = token_response.json()['access_token']
        
        # Get LinkedIn profile data
        headers = {'Authorization': f'Bearer {access_token}'}
        
        # Get basic profile
        profile_response = requests.get(
            'https://api.linkedin.com/v2/people/~?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))',
            headers=headers
        )
        
        # Get email
        email_response = requests.get(
            'https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))',
            headers=headers
        )
        
        if profile_response.status_code != 200 or email_response.status_code != 200:
            raise HTTPException(status_code=400, detail="Failed to get LinkedIn profile")
        
        profile_data = profile_response.json()
        email_data = email_response.json()
        
        # Extract profile information
        linkedin_profile = {
            "linkedin_id": profile_data['id'],
            "first_name": profile_data['firstName']['localized'].get('en_US', ''),
            "last_name": profile_data['lastName']['localized'].get('en_US', ''),
            "email": email_data['elements'][0]['handle~']['emailAddress'] if email_data.get('elements') else '',
            "profile_picture": None,  # We'll implement this later
            "headline": "",  # We'll get this from positions endpoint
            "summary": "",
            "positions": [],  # We'll get this from positions endpoint
            "connected_at": datetime.utcnow()
        }
        
        # Store LinkedIn profile data
        await db.linkedin_profiles.update_one(
            {"user_id": state},
            {"$set": {**linkedin_profile, "user_id": state}},
            upsert=True
        )
        
        # Update user to indicate LinkedIn is connected
        await db.users.update_one(
            {"id": state},
            {"$set": {"linkedin_connected": True, "linkedin_updated_at": datetime.utcnow()}}
        )
        
        # Redirect back to frontend with success
        frontend_url = os.environ.get('FRONTEND_URL', 'http://localhost:3002')
        return RedirectResponse(url=f"{frontend_url}/integrations?linkedin=connected")
        
    except Exception as e:
        logger.error(f"LinkedIn callback error: {e}")
        frontend_url = os.environ.get('FRONTEND_URL', 'http://localhost:3002')
        return RedirectResponse(url=f"{frontend_url}/integrations?linkedin=error")

@api_router.get("/integrations/linkedin/profile")
async def get_linkedin_profile(current_user: User = Depends(get_current_user)):
    """Get user's connected LinkedIn profile data"""
    linkedin_data = await db.linkedin_profiles.find_one({"user_id": current_user.id})
    
    if not linkedin_data:
        raise HTTPException(status_code=404, detail="LinkedIn profile not connected")
    
    # Remove MongoDB _id field
    linkedin_data.pop('_id', None)
    return linkedin_data

@api_router.delete("/integrations/linkedin")
async def disconnect_linkedin(current_user: User = Depends(get_current_user)):
    """Disconnect LinkedIn integration"""
    # Remove LinkedIn profile data
    await db.linkedin_profiles.delete_one({"user_id": current_user.id})
    
    # Update user status
    await db.users.update_one(
        {"id": current_user.id},
        {"$set": {"linkedin_connected": False}, "$unset": {"linkedin_updated_at": 1}}
    )
    
    return {"message": "LinkedIn integration disconnected successfully"}

@api_router.post("/integrations/linkedin/sync-experience")
async def sync_linkedin_experience(current_user: User = Depends(get_current_user)):
    """Sync LinkedIn experience to user profile"""
    # Get LinkedIn profile data
    linkedin_data = await db.linkedin_profiles.find_one({"user_id": current_user.id})
    
    if not linkedin_data:
        raise HTTPException(status_code=404, detail="LinkedIn profile not connected")
    
    # Get user's current profile
    profile = await db.profiles.find_one({"user_id": current_user.id})
    
    if not profile:
        raise HTTPException(status_code=404, detail="User profile not found")
    
    # Convert LinkedIn positions to our experience format
    linkedin_experiences = []
    for position in linkedin_data.get('positions', []):
        experience = {
            "id": str(uuid.uuid4()),
            "position": position.get('title', ''),
            "company": position.get('companyName', ''),
            "start_date": position.get('startDate', datetime.utcnow()),
            "end_date": position.get('endDate') if not position.get('current', False) else None,
            "description": position.get('summary', ''),
            "location": position.get('location', ''),
            "still_working": position.get('current', False),
            "source": "linkedin"
        }
        linkedin_experiences.append(experience)
    
    # Update profile with LinkedIn experiences
    existing_experience = profile.get('experience', [])
    
    # Remove any existing LinkedIn-sourced experiences
    existing_experience = [exp for exp in existing_experience if exp.get('source') != 'linkedin']
    
    # Add new LinkedIn experiences
    updated_experience = existing_experience + linkedin_experiences
    
    await db.profiles.update_one(
        {"user_id": current_user.id},
        {"$set": {"experience": updated_experience, "linkedin_synced_at": datetime.utcnow()}}
    )
    
    return {
        "message": f"Successfully synced {len(linkedin_experiences)} experiences from LinkedIn",
        "experiences_added": len(linkedin_experiences)
    }

# LinkedIn Learning Integration
class LinkedInLearningCertificate(BaseModel):
    certificate_id: str
    course_name: str
    course_url: str
    completion_date: datetime
    skills: List[str] = []
    verified: bool = True
    verification_date: datetime = Field(default_factory=datetime.utcnow)

@api_router.post("/integrations/linkedin-learning/import-certificates")
async def import_linkedin_learning_certificates(current_user: User = Depends(get_current_user)):
    """Import LinkedIn Learning certificates"""
    # Check if LinkedIn is connected
    linkedin_data = await db.linkedin_profiles.find_one({"user_id": current_user.id})
    
    # Allow mock certificates even without LinkedIn connection for demo purposes
    if not linkedin_data:
        print(f"No LinkedIn profile found for user {current_user.id}, creating demo mock data")
    
    # In a real implementation, we would call LinkedIn Learning API
    # For now, let's simulate some agricultural certificates
    sample_certificates = [
        {
            "certificate_id": str(uuid.uuid4()),
            "course_name": "Sustainable Agriculture Practices",
            "course_url": "https://www.linkedin.com/learning/sustainable-agriculture-practices",
            "completion_date": datetime(2024, 1, 15),
            "skills": ["Sustainable Farming", "Crop Rotation", "Soil Management"],
            "verified": True,
            "verification_date": datetime.utcnow()
        },
        {
            "certificate_id": str(uuid.uuid4()),
            "course_name": "Agricultural Technology and Innovation", 
            "course_url": "https://www.linkedin.com/learning/agricultural-technology-innovation",
            "completion_date": datetime(2024, 2, 20),
            "skills": ["AgTech", "Precision Agriculture", "Farm Management Software"],
            "verified": True,
            "verification_date": datetime.utcnow()
        },
        {
            "certificate_id": str(uuid.uuid4()),
            "course_name": "Organic Farming Certification Prep",
            "course_url": "https://www.linkedin.com/learning/organic-farming-certification",
            "completion_date": datetime(2024, 3, 10),
            "skills": ["Organic Farming", "USDA Organic Standards", "Pest Management"],
            "verified": True,
            "verification_date": datetime.utcnow()
        }
    ]
    
    # Store certificates in database
    for cert in sample_certificates:
        cert["user_id"] = current_user.id
        cert["source"] = "linkedin_learning"
    
    # Remove existing LinkedIn Learning certificates
    await db.certificates.delete_many({"user_id": current_user.id, "source": "linkedin_learning"})
    
    # Insert new certificates
    if sample_certificates:
        await db.certificates.insert_many(sample_certificates)
    
    # Update profile with new skills from certificates
    profile = await db.profiles.find_one({"user_id": current_user.id})
    if profile:
        existing_skills = profile.get('skills', [])
        existing_skill_names = [skill.get('name', '') for skill in existing_skills]
        
        new_skills = []
        for cert in sample_certificates:
            for skill_name in cert['skills']:
                if skill_name not in existing_skill_names:
                    new_skills.append({
                        "id": str(uuid.uuid4()),
                        "name": skill_name,
                        "category": "Agricultural Education",
                        "verified": True,
                        "verification_source": "linkedin_learning",
                        "certificate_id": cert['certificate_id']
                    })
                    existing_skill_names.append(skill_name)
        
        if new_skills:
            updated_skills = existing_skills + new_skills
            await db.profiles.update_one(
                {"user_id": current_user.id},
                {"$set": {"skills": updated_skills, "linkedin_learning_synced_at": datetime.utcnow()}}
            )
    
    return {
        "message": f"Successfully imported {len(sample_certificates)} LinkedIn Learning certificates",
        "certificates_imported": len(sample_certificates),
        "skills_added": len([skill for cert in sample_certificates for skill in cert['skills']])
    }

@api_router.get("/integrations/linkedin-learning/certificates")
async def get_linkedin_learning_certificates(current_user: User = Depends(get_current_user)):
    """Get user's LinkedIn Learning certificates"""
    certificates = await db.certificates.find(
        {"user_id": current_user.id, "source": "linkedin_learning"}
    ).to_list(100)
    
    # Remove MongoDB _id fields
    for cert in certificates:
        cert.pop('_id', None)
    
    return {"certificates": certificates, "total": len(certificates)}

# Profile endpoints
@api_router.post("/profiles", response_model=Profile)
async def create_profile(profile_data: ProfileBase, current_user: User = Depends(get_current_user)):
    # Check if profile already exists
    existing_profile = await db.profiles.find_one({"user_id": current_user.id})
    if existing_profile:
        raise HTTPException(status_code=400, detail="Profile already exists")
    
    profile = Profile(**profile_data.dict())
    profile.user_id = current_user.id
    
    await db.profiles.insert_one(profile.dict())
    
    # Update user's profile_completed status
    await db.users.update_one(
        {"id": current_user.id},
        {"$set": {"profile_completed": True}}
    )
    
    return profile

@api_router.get("/profiles/{user_id}", response_model=Profile)
async def get_profile(user_id: str, current_user: User = Depends(get_current_user)):
    profile = await db.profiles.find_one({"user_id": user_id})
    if not profile:
        raise HTTPException(status_code=404, detail="Profile not found")
    
    return Profile(**profile)

@api_router.put("/profiles/{user_id}", response_model=Profile)
async def update_profile(user_id: str, profile_data: ProfileBase, current_user: User = Depends(get_current_user)):
    if current_user.id != user_id:
        raise HTTPException(status_code=403, detail="Can only update own profile")
    
    profile_dict = profile_data.dict()
    profile_dict["updated_at"] = datetime.utcnow()
    
    await db.profiles.update_one(
        {"user_id": user_id},
        {"$set": profile_dict}
    )
    
    updated_profile = await db.profiles.find_one({"user_id": user_id})
    return Profile(**updated_profile)

# Services endpoints
@api_router.post("/services", response_model=Service)
async def create_service(service_data: ServiceBase, current_user: User = Depends(get_current_user)):
    service_dict = service_data.dict()
    service_dict["provider_id"] = current_user.id
    service = Service(**service_dict)
    
    await db.services.insert_one(service.dict())
    return service

@api_router.get("/services", response_model=List[Service])
async def get_services(skip: int = 0, limit: int = 10, service_type: Optional[ServiceType] = None):
    query = {"active": True}
    if service_type:
        query["service_type"] = service_type
    
    services = await db.services.find(query).skip(skip).limit(limit).to_list(limit)
    return [Service(**service) for service in services]

@api_router.get("/services/{service_id}", response_model=Service)
async def get_service(service_id: str):
    service = await db.services.find_one({"id": service_id})
    if not service:
        raise HTTPException(status_code=404, detail="Service not found")
    
    return Service(**service)

# Project Experience endpoints
@api_router.post("/projects", response_model=ProjectExperience)
async def create_project_experience(project_data: ProjectExperience, current_user: User = Depends(get_current_user)):
    """Create a new project experience"""
    project = ProjectExperience(**project_data.dict())
    project.created_by = current_user.id
    
    # Ensure the current user is included in collaborators
    if current_user.id not in project.collaborators:
        project.collaborators.append(current_user.id)
    
    await db.project_experiences.insert_one(project.dict())
    return project

@api_router.get("/projects", response_model=List[ProjectExperience])
async def get_user_projects(current_user: User = Depends(get_current_user)):
    """Get projects where the current user is a collaborator"""
    projects = await db.project_experiences.find({
        "$or": [
            {"created_by": current_user.id},
            {"collaborators": {"$in": [current_user.id]}}
        ]
    }).to_list(100)
    return [ProjectExperience(**project) for project in projects]

@api_router.get("/projects/{project_id}", response_model=ProjectExperience)
async def get_project_experience(project_id: str, current_user: User = Depends(get_current_user)):
    """Get a specific project experience"""
    project = await db.project_experiences.find_one({"id": project_id})
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")
    
    # Check if user has access to this project
    if current_user.id not in project.get("collaborators", []) and project.get("created_by") != current_user.id:
        raise HTTPException(status_code=403, detail="Access denied to this project")
    
    return ProjectExperience(**project)

@api_router.post("/projects/{project_id}/invite-collaborator")
async def invite_project_collaborator(
    project_id: str, 
    collaborator_user_id: str, 
    current_user: User = Depends(get_current_user)
):
    """Invite someone to be recognized as a collaborator on a project"""
    project = await db.project_experiences.find_one({"id": project_id})
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")
    
    # Only project creator can invite collaborators
    if project.get("created_by") != current_user.id:
        raise HTTPException(status_code=403, detail="Only project creator can invite collaborators")
    
    # Check if user exists
    collaborator = await db.users.find_one({"id": collaborator_user_id})
    if not collaborator:
        raise HTTPException(status_code=404, detail="User not found")
    
    # Add to collaborators if not already there
    if collaborator_user_id not in project.get("collaborators", []):
        await db.project_experiences.update_one(
            {"id": project_id},
            {"$addToSet": {"collaborators": collaborator_user_id}}
        )
    
    return {"message": f"Collaborator {collaborator['name']} added to project"}

@api_router.post("/projects/validate", response_model=ProjectValidationRequest)
async def create_project_validation(validation_data: ProjectValidationRequest, current_user: User = Depends(get_current_user)):
    """Create a project-based validation"""
    # Verify the project exists and validator has access
    project = await db.project_experiences.find_one({"id": validation_data.project_experience_id})
    if not project:
        raise HTTPException(status_code=404, detail="Project not found")
    
    if current_user.id not in project.get("collaborators", []):
        raise HTTPException(status_code=403, detail="You must be a project collaborator to validate others")
    
    # Verify the validated user was also part of the project
    if validation_data.validated_user_id not in project.get("collaborators", []):
        raise HTTPException(status_code=400, detail="You can only validate people who worked on the same project")
    
    validation = ProjectValidationRequest(**validation_data.dict())
    validation.validator_id = current_user.id
    
    await db.project_validations.insert_one(validation.dict())
    return validation

@api_router.get("/projects/validations/received", response_model=List[ProjectValidationRequest])
async def get_received_project_validations(current_user: User = Depends(get_current_user)):
    """Get project validations received by current user"""
    validations = await db.project_validations.find({"validated_user_id": current_user.id}).to_list(100)
    return [ProjectValidationRequest(**validation) for validation in validations]

@api_router.get("/projects/validations/given", response_model=List[ProjectValidationRequest])
async def get_given_project_validations(current_user: User = Depends(get_current_user)):
    """Get project validations given by current user"""
    validations = await db.project_validations.find({"validator_id": current_user.id}).to_list(100)
    return [ProjectValidationRequest(**validation) for validation in validations]

@api_router.put("/projects/validations/{validation_id}/approve")
async def approve_project_validation(validation_id: str, current_user: User = Depends(get_current_user)):
    """Approve a project validation"""
    validation = await db.project_validations.find_one({"id": validation_id})
    if not validation:
        raise HTTPException(status_code=404, detail="Validation not found")
    
    if validation["validated_user_id"] != current_user.id:
        raise HTTPException(status_code=403, detail="Can only approve your own validations")
    
    await db.project_validations.update_one(
        {"id": validation_id},
        {"$set": {"status": ValidationStatus.APPROVED, "updated_at": datetime.utcnow()}}
    )
    
    # Update user's validation count
    await db.profiles.update_one(
        {"user_id": current_user.id},
        {"$inc": {"total_validations": 1}}
    )
    
    return {"message": "Project validation approved"}

# Enhanced Validation endpoints with external tagging
@api_router.post("/validations/enhanced", response_model=EnhancedValidationRequest)
async def create_enhanced_validation(validation_data: EnhancedValidationRequest, current_user: User = Depends(get_current_user)):
    """Create enhanced validation with internal or external user tagging"""
    validation = EnhancedValidationRequest(**validation_data.dict())
    validation.validator_id = current_user.id
    
    # Validate that either internal user or external profile is provided
    if not validation.validated_user_id and not validation.external_profile:
        raise HTTPException(status_code=400, detail="Must provide either validated_user_id or external_profile")
    
    # If internal user, verify they exist
    if validation.validated_user_id:
        user_exists = await db.users.find_one({"id": validation.validated_user_id})
        if not user_exists:
            raise HTTPException(status_code=404, detail="Validated user not found")
    
    # If external profile, handle invitation logic
    if validation.external_profile:
        await handle_external_user_invitation(validation.external_profile, validation.id)
        validation.external_invited = True
    
    await db.enhanced_validations.insert_one(validation.dict())
    return validation

async def handle_external_user_invitation(external_profile: ExternalProfile, validation_id: str):
    """Handle invitation logic for external users"""
    try:
        if external_profile.platform == "linkedin" and external_profile.profile_url:
            # Create LinkedIn invitation (mock for now)
            print(f"📧 LinkedIn invitation would be sent to: {external_profile.profile_url}")
            
        elif external_profile.platform == "email" and external_profile.email:
            # Create email invitation (mock for now)
            print(f"📧 Email invitation would be sent to: {external_profile.email}")
            
        # In production, integrate with:
        # - LinkedIn API for invitations
        # - Email service (SendGrid, etc.)
        # - SMS service for notifications
        
        # Store pending invitation
        invitation = {
            "validation_id": validation_id,
            "external_profile": external_profile.dict(),
            "status": "sent",
            "created_at": datetime.utcnow()
        }
        await db.external_invitations.insert_one(invitation)
        
    except Exception as e:
        print(f"❌ Error sending invitation: {e}")

@api_router.get("/search/external-profiles")
async def search_external_profiles(
    q: Optional[str] = None,
    platform: Optional[str] = None,
    current_user: User = Depends(get_current_user),
    limit: int = 10
):
    """Search for external profiles (LinkedIn, etc.)"""
    if not q:
        return []
    
    # Mock LinkedIn search results for demo
    if platform == "linkedin" or not platform:
        mock_linkedin_results = [
            {
                "platform": "linkedin",
                "platform_id": "maria-agronoma-123",
                "profile_url": f"https://linkedin.com/in/maria-agronoma-123",
                "name": "Dr. María Fernández",
                "title": "Agrónoma Especialista en Riego",
                "company": "AgroConsult España",
                "email": None
            },
            {
                "platform": "linkedin", 
                "platform_id": "carlos-ingeniero-456",
                "profile_url": f"https://linkedin.com/in/carlos-ingeniero-456",
                "name": "Carlos Rodríguez",
                "title": "Ingeniero Agrícola",
                "company": "TecnoAgro Solutions",
                "email": None
            }
        ]
        
        # Filter by search query
        filtered_results = []
        for result in mock_linkedin_results:
            if q.lower() in result["name"].lower() or q.lower() in result["title"].lower():
                filtered_results.append(result)
        
        return filtered_results[:limit]
    
    return []

@api_router.get("/validations/enhanced/received", response_model=List[EnhancedValidationRequest])
async def get_received_enhanced_validations(current_user: User = Depends(get_current_user)):
    """Get enhanced validations received by current user"""
    validations = await db.enhanced_validations.find({"validated_user_id": current_user.id}).to_list(100)
    return [EnhancedValidationRequest(**validation) for validation in validations]

@api_router.get("/validations/enhanced/given", response_model=List[EnhancedValidationRequest])
async def get_given_enhanced_validations(current_user: User = Depends(get_current_user)):
    """Get enhanced validations given by current user"""
    validations = await db.enhanced_validations.find({"validator_id": current_user.id}).to_list(100)
    return [EnhancedValidationRequest(**validation) for validation in validations]

@api_router.get("/external-invitations/pending")
async def get_pending_external_invitations(current_user: User = Depends(get_current_user)):
    """Get pending external invitations sent by current user"""
    invitations = await db.external_invitations.find({
        "external_profile.email": {"$exists": True},
        "status": "sent"
    }).to_list(100)
    
    return invitations

@api_router.post("/external-invitations/{invitation_id}/accept")
async def accept_external_invitation(invitation_id: str, current_user: User = Depends(get_current_user)):
    """Accept an external invitation and link to SHARKNO account"""
    invitation = await db.external_invitations.find_one({"_id": invitation_id})
    if not invitation:
        raise HTTPException(status_code=404, detail="Invitation not found")
    
    # Link external profile to SHARKNO user
    await db.external_invitations.update_one(
        {"_id": invitation_id},
        {"$set": {"status": "accepted", "sharkno_user_id": current_user.id}}
    )
    
    # Update the validation to point to the SHARKNO user
    await db.enhanced_validations.update_one(
        {"id": invitation["validation_id"]},
        {"$set": {"validated_user_id": current_user.id, "external_profile.sharkno_user_id": current_user.id}}
    )
    
    return {"message": "Invitation accepted and profile linked"}

# Enhanced Validation endpoints (keeping original for backward compatibility)
# Enhanced Validation endpoints (keeping original for backward compatibility)
@api_router.post("/validations", response_model=ValidationRequest)
async def create_validation(validation_data: ValidationRequest, current_user: User = Depends(get_current_user)):
    validation = ValidationRequest(**validation_data.dict())
    validation.validator_id = current_user.id
    
    await db.validations.insert_one(validation.dict())
    return validation

@api_router.get("/validations", response_model=List[ValidationRequest])
async def get_validations(current_user: User = Depends(get_current_user)):
    validations = await db.validations.find({"validated_user_id": current_user.id}).to_list(100)
    return [ValidationRequest(**validation) for validation in validations]

@api_router.get("/validations/given", response_model=List[ValidationRequest])
async def get_given_validations(current_user: User = Depends(get_current_user)):
    """Get validations given by current user"""
    validations = await db.validations.find({"validator_id": current_user.id}).to_list(100)
    return [ValidationRequest(**validation) for validation in validations]

@api_router.put("/validations/{validation_id}/approve")
async def approve_validation(validation_id: str, current_user: User = Depends(get_current_user)):
    validation = await db.validations.find_one({"id": validation_id})
    if not validation:
        raise HTTPException(status_code=404, detail="Validation not found")
    
    if validation["validated_user_id"] != current_user.id:
        raise HTTPException(status_code=403, detail="Can only approve your own validations")
    
    await db.validations.update_one(
        {"id": validation_id},
        {"$set": {"status": ValidationStatus.APPROVED, "updated_at": datetime.utcnow()}}
    )
    
    return {"message": "Validation approved"}

# Search endpoint for finding project collaborators
@api_router.get("/search/collaborators", response_model=List[dict])
async def search_potential_collaborators(
    q: Optional[str] = None,
    current_user: User = Depends(get_current_user),
    limit: int = 10
):
    """Search for users to add as project collaborators"""
    query = {"id": {"$ne": current_user.id}}  # Exclude current user
    
    if q:
        # Search in user info
        users = await db.users.find({
            "$and": [
                query,
                {
                    "$or": [
                        {"name": {"$regex": q, "$options": "i"}},
                        {"email": {"$regex": q, "$options": "i"}}
                    ]
                }
            ]
        }).limit(limit).to_list(limit)
        
        # Also search in profiles
        profiles = await db.profiles.find({
            "$or": [
                {"title": {"$regex": q, "$options": "i"}},
                {"bio": {"$regex": q, "$options": "i"}},
                {"skills.name": {"$regex": q, "$options": "i"}}
            ]
        }).limit(limit).to_list(limit)
        
        # Combine results
        user_results = []
        for user in users:
            profile = await db.profiles.find_one({"user_id": user["id"]})
            user_results.append({
                "user_id": user["id"],
                "name": user["name"],
                "email": user["email"],
                "role": user["role"],
                "title": profile.get("title", "") if profile else "",
                "bio": profile.get("bio", "") if profile else ""
            })
        
        for profile in profiles:
            # Check if user not already in results
            if not any(u["user_id"] == profile["user_id"] for u in user_results):
                user = await db.users.find_one({"id": profile["user_id"]})
                if user:
                    user_results.append({
                        "user_id": user["id"],
                        "name": user["name"],
                        "email": user["email"],
                        "role": user["role"],
                        "title": profile.get("title", ""),
                        "bio": profile.get("bio", "")
                    })
        
        return user_results[:limit]
    
    return []

# Reviews endpoints
@api_router.post("/reviews", response_model=Review)
async def create_review(review_data: ReviewBase, current_user: User = Depends(get_current_user)):
    review = Review(**review_data.dict())
    review.reviewer_id = current_user.id
    
    await db.reviews.insert_one(review.dict())
    return review

@api_router.get("/reviews/user/{user_id}", response_model=List[Review])
async def get_user_reviews(user_id: str):
    reviews = await db.reviews.find({"reviewed_user_id": user_id}).to_list(100)
    return [Review(**review) for review in reviews]

# Search endpoints
@api_router.get("/search/profiles", response_model=List[Profile])
async def search_profiles(
    q: Optional[str] = None,
    role: Optional[UserRole] = None,
    location: Optional[str] = None,
    skip: int = 0,
    limit: int = 10
):
    query = {}
    
    if q:
        query["$or"] = [
            {"title": {"$regex": q, "$options": "i"}},
            {"bio": {"$regex": q, "$options": "i"}},
            {"skills.name": {"$regex": q, "$options": "i"}}
        ]
    
    if location:
        query["address.city"] = {"$regex": location, "$options": "i"}
    
    profiles = await db.profiles.find(query).skip(skip).limit(limit).to_list(limit)
    return [Profile(**profile) for profile in profiles]

# Health check (also add to API router)
@api_router.get("/health")
async def api_health_check():
    return {"status": "healthy", "service": "SharkNo API"}

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

# Include router
app.include_router(api_router)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

@app.on_event("shutdown")
async def shutdown_db_client():
    client.close()