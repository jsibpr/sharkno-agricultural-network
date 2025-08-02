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

class ValidationRequest(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    skill_id: str
    validator_id: str
    validated_user_id: str
    description: str
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
        frontend_url = os.environ.get('FRONTEND_URL', 'http://localhost:3001')
        return RedirectResponse(url=f"{frontend_url}/profile?linkedin=connected")
        
    except Exception as e:
        logger.error(f"LinkedIn callback error: {e}")
        frontend_url = os.environ.get('FRONTEND_URL', 'http://localhost:3001')
        return RedirectResponse(url=f"{frontend_url}/profile?linkedin=error")

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
    
    if not linkedin_data:
        raise HTTPException(status_code=404, detail="LinkedIn profile not connected")
    
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
    service = Service(**service_data.dict())
    service.provider_id = current_user.id
    
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

# Validation endpoints
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