import requests
import sys
import json
from datetime import datetime, timedelta
from typing import Dict, Any
import pymongo
import os

class SharkNoAPITester:
    def __init__(self, base_url="https://a9c611d5-645d-4aea-9fa4-24b37432de0d.preview.emergentagent.com"):
        self.base_url = base_url
        self.api_url = f"{base_url}/api"
        self.token = None
        self.user_id = None
        self.tests_run = 0
        self.tests_passed = 0
        self.test_data = {}

    def log_test(self, name: str, success: bool, details: str = ""):
        """Log test results"""
        self.tests_run += 1
        if success:
            self.tests_passed += 1
            print(f"✅ {name} - PASSED {details}")
        else:
            print(f"❌ {name} - FAILED {details}")

    def make_request(self, method: str, endpoint: str, data: Dict = None, expected_status: int = 200) -> tuple:
        """Make HTTP request and return success status and response"""
        url = f"{self.api_url}/{endpoint}"
        headers = {'Content-Type': 'application/json'}
        
        if self.token:
            headers['Authorization'] = f'Bearer {self.token}'

        try:
            if method == 'GET':
                response = requests.get(url, headers=headers)
            elif method == 'POST':
                response = requests.post(url, json=data, headers=headers)
            elif method == 'PUT':
                response = requests.put(url, json=data, headers=headers)
            elif method == 'DELETE':
                response = requests.delete(url, headers=headers)

            success = response.status_code == expected_status
            response_data = response.json() if response.content else {}
            
            return success, response_data, response.status_code
        except Exception as e:
            return False, {"error": str(e)}, 0

    def test_health_check(self):
        """Test health endpoint"""
        success, data, status = self.make_request('GET', '../health', expected_status=200)
        self.log_test("Health Check", success, f"Status: {status}")
        return success

    def test_user_registration(self):
        """Test user registration with different agricultural roles"""
        test_cases = [
            {
                "role": "farmer",
                "email": f"farmer_{datetime.now().strftime('%H%M%S')}@test.com",
                "name": "John Farmer",
                "password": "TestPass123!"
            },
            {
                "role": "consultant", 
                "email": f"consultant_{datetime.now().strftime('%H%M%S')}@test.com",
                "name": "Jane Consultant",
                "password": "TestPass123!"
            },
            {
                "role": "veterinarian",
                "email": f"vet_{datetime.now().strftime('%H%M%S')}@test.com", 
                "name": "Dr. Smith",
                "password": "TestPass123!"
            }
        ]

        for i, user_data in enumerate(test_cases):
            success, response, status = self.make_request('POST', 'auth/register', user_data, 200)
            
            if success and 'access_token' in response:
                if i == 0:  # Store first user for subsequent tests
                    self.token = response['access_token']
                    self.user_id = response['user']['id']
                    self.test_data['main_user'] = response['user']
                    self.test_data['main_user']['password'] = user_data['password']
                
                self.log_test(f"Register {user_data['role']}", True, f"User ID: {response['user']['id']}")
            else:
                self.log_test(f"Register {user_data['role']}", False, f"Status: {status}, Response: {response}")

        return self.token is not None

    def test_user_login(self):
        """Test user login"""
        if not self.test_data.get('main_user'):
            self.log_test("Login", False, "No user data available")
            return False

        login_data = {
            "email": self.test_data['main_user']['email'],
            "password": self.test_data['main_user']['password']
        }

        success, response, status = self.make_request('POST', 'auth/login', login_data, 200)
        
        if success and 'access_token' in response:
            self.token = response['access_token']
            self.log_test("Login", True, f"Token received")
            return True
        else:
            self.log_test("Login", False, f"Status: {status}, Response: {response}")
            return False

    def test_get_current_user(self):
        """Test getting current user info"""
        success, response, status = self.make_request('GET', 'auth/me', expected_status=200)
        
        if success and 'id' in response:
            self.log_test("Get Current User", True, f"User: {response['name']}")
            return True
        else:
            self.log_test("Get Current User", False, f"Status: {status}")
            return False

    def test_create_profile(self):
        """Test creating agricultural professional profile"""
        profile_data = {
            "user_id": self.user_id,
            "profile_type": "individual",
            "title": "Senior Agricultural Consultant",
            "bio": "Experienced agricultural consultant specializing in sustainable farming practices and crop optimization.",
            "phone": "+1-555-0123",
            "address": {
                "street": "123 Farm Road",
                "city": "Des Moines",
                "state": "Iowa", 
                "country": "USA",
                "postal_code": "50309"
            },
            "website": "https://johnfarmer.com",
            "skills": [
                {
                    "name": "Crop Rotation",
                    "category": "Agronomy",
                    "verified": False
                },
                {
                    "name": "Soil Analysis",
                    "category": "Soil Science",
                    "verified": False
                },
                {
                    "name": "Precision Agriculture",
                    "category": "Technology",
                    "verified": False
                }
            ],
            "experience": [
                {
                    "position": "Farm Manager",
                    "company": "Green Valley Farms",
                    "start_date": "2020-01-01T00:00:00Z",
                    "end_date": "2023-12-31T00:00:00Z",
                    "description": "Managed 500-acre corn and soybean operation",
                    "location": "Iowa, USA",
                    "still_working": False
                }
            ],
            "certifications": [
                {
                    "name": "Certified Crop Advisor",
                    "issuing_organization": "American Society of Agronomy",
                    "issue_date": "2019-06-01T00:00:00Z",
                    "credential_id": "CCA-12345"
                }
            ]
        }

        success, response, status = self.make_request('POST', 'profiles', profile_data, 200)
        
        if success and 'id' in response:
            self.test_data['profile_id'] = response['id']
            self.log_test("Create Profile", True, f"Profile ID: {response['id']}")
            return True
        else:
            self.log_test("Create Profile", False, f"Status: {status}, Response: {response}")
            return False

    def test_get_profile(self):
        """Test getting profile"""
        if not self.user_id:
            self.log_test("Get Profile", False, "No user ID available")
            return False

        success, response, status = self.make_request('GET', f'profiles/{self.user_id}', expected_status=200)
        
        if success and 'title' in response:
            self.log_test("Get Profile", True, f"Title: {response['title']}")
            return True
        else:
            self.log_test("Get Profile", False, f"Status: {status}")
            return False

    def test_update_profile(self):
        """Test updating profile"""
        if not self.user_id:
            self.log_test("Update Profile", False, "No user ID available")
            return False

        update_data = {
            "user_id": self.user_id,
            "profile_type": "individual",
            "title": "Senior Agricultural Consultant - Updated",
            "bio": "Updated bio with more experience in sustainable farming.",
            "phone": "+1-555-0124",
            "skills": [
                {
                    "name": "Organic Farming",
                    "category": "Sustainable Agriculture",
                    "verified": True
                }
            ],
            "experience": [],
            "certifications": []
        }

        success, response, status = self.make_request('PUT', f'profiles/{self.user_id}', update_data, 200)
        
        if success and 'title' in response:
            self.log_test("Update Profile", True, f"Updated title: {response['title']}")
            return True
        else:
            self.log_test("Update Profile", False, f"Status: {status}")
            return False

    def test_create_service(self):
        """Test creating agricultural services"""
        services = [
            {
                "title": "Crop Consultation Services",
                "description": "Professional crop consultation for optimal yield and sustainable practices",
                "service_type": "consultation",
                "price_min": 100.0,
                "price_max": 500.0,
                "currency": "USD",
                "location": "Iowa, USA",
                "experience_level": "expert",
                "skills_required": ["Crop Management", "Soil Analysis"],
                "availability": "Monday-Friday, 9AM-5PM"
            },
            {
                "title": "Veterinary Services for Livestock",
                "description": "Complete veterinary care for cattle, pigs, and poultry",
                "service_type": "veterinary",
                "price_min": 75.0,
                "price_max": 300.0,
                "currency": "USD",
                "location": "Rural Iowa",
                "experience_level": "expert",
                "skills_required": ["Veterinary Medicine", "Livestock Care"],
                "availability": "24/7 Emergency Available"
            }
        ]

        for service_data in services:
            success, response, status = self.make_request('POST', 'services', service_data, 200)
            
            if success and 'id' in response:
                if not hasattr(self, 'service_ids'):
                    self.service_ids = []
                self.service_ids.append(response['id'])
                self.log_test(f"Create Service: {service_data['service_type']}", True, f"Service ID: {response['id']}")
            else:
                self.log_test(f"Create Service: {service_data['service_type']}", False, f"Status: {status}")

        return hasattr(self, 'service_ids') and len(self.service_ids) > 0

    def test_get_services(self):
        """Test getting services list"""
        success, response, status = self.make_request('GET', 'services?limit=20', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Get Services", True, f"Found {len(response)} services")
            return True
        else:
            self.log_test("Get Services", False, f"Status: {status}")
            return False

    def test_get_services_by_type(self):
        """Test filtering services by type"""
        service_types = ["consultation", "veterinary", "agronomic_advice"]
        
        for service_type in service_types:
            success, response, status = self.make_request('GET', f'services?service_type={service_type}', expected_status=200)
            
            if success and isinstance(response, list):
                self.log_test(f"Get Services by Type: {service_type}", True, f"Found {len(response)} services")
            else:
                self.log_test(f"Get Services by Type: {service_type}", False, f"Status: {status}")

    def test_get_single_service(self):
        """Test getting a single service"""
        if not hasattr(self, 'service_ids') or not self.service_ids:
            self.log_test("Get Single Service", False, "No service IDs available")
            return False

        service_id = self.service_ids[0]
        success, response, status = self.make_request('GET', f'services/{service_id}', expected_status=200)
        
        if success and 'title' in response:
            self.log_test("Get Single Service", True, f"Service: {response['title']}")
            return True
        else:
            self.log_test("Get Single Service", False, f"Status: {status}")
            return False

    def test_create_validation(self):
        """Test creating skill validation"""
        validation_data = {
            "skill_id": "skill-123",
            "validator_id": self.user_id,
            "validated_user_id": self.user_id,
            "description": "Validated expertise in crop rotation techniques through field demonstration",
            "status": "pending"
        }

        success, response, status = self.make_request('POST', 'validations', validation_data, 200)
        
        if success and 'id' in response:
            self.test_data['validation_id'] = response['id']
            self.log_test("Create Validation", True, f"Validation ID: {response['id']}")
            return True
        else:
            self.log_test("Create Validation", False, f"Status: {status}")
            return False

    def test_get_validations(self):
        """Test getting validations"""
        success, response, status = self.make_request('GET', 'validations', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Get Validations", True, f"Found {len(response)} validations")
            return True
        else:
            self.log_test("Get Validations", False, f"Status: {status}")
            return False

    def test_approve_validation(self):
        """Test approving validation"""
        if not self.test_data.get('validation_id'):
            self.log_test("Approve Validation", False, "No validation ID available")
            return False

        validation_id = self.test_data['validation_id']
        success, response, status = self.make_request('PUT', f'validations/{validation_id}/approve', expected_status=200)
        
        if success and 'message' in response:
            self.log_test("Approve Validation", True, response['message'])
            return True
        else:
            self.log_test("Approve Validation", False, f"Status: {status}")
            return False

    def test_create_review(self):
        """Test creating review"""
        review_data = {
            "reviewer_id": self.user_id,
            "reviewed_user_id": self.user_id,
            "rating": 5,
            "comment": "Excellent agricultural consultation services. Very knowledgeable and professional."
        }

        success, response, status = self.make_request('POST', 'reviews', review_data, 200)
        
        if success and 'id' in response:
            self.test_data['review_id'] = response['id']
            self.log_test("Create Review", True, f"Review ID: {response['id']}")
            return True
        else:
            self.log_test("Create Review", False, f"Status: {status}")
            return False

    def test_get_user_reviews(self):
        """Test getting user reviews"""
        if not self.user_id:
            self.log_test("Get User Reviews", False, "No user ID available")
            return False

        success, response, status = self.make_request('GET', f'reviews/user/{self.user_id}', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Get User Reviews", True, f"Found {len(response)} reviews")
            return True
        else:
            self.log_test("Get User Reviews", False, f"Status: {status}")
            return False

    def test_search_profiles(self):
        """Test searching profiles"""
        search_queries = [
            "?q=agricultural",
            "?q=consultant", 
            "?role=farmer",
            "?location=Iowa",
            "?q=crop&limit=5"
        ]

        for query in search_queries:
            success, response, status = self.make_request('GET', f'search/profiles{query}', expected_status=200)
            
            if success and isinstance(response, list):
                self.log_test(f"Search Profiles: {query}", True, f"Found {len(response)} profiles")
            else:
                self.log_test(f"Search Profiles: {query}", False, f"Status: {status}")

    def setup_mock_linkedin_profile(self):
        """Set up a mock LinkedIn profile for testing"""
        try:
            # Connect to MongoDB directly to insert mock LinkedIn profile
            mongo_client = pymongo.MongoClient("mongodb://localhost:27017")
            db = mongo_client["sharkno_agricultural"]
            
            # Create mock LinkedIn profile
            linkedin_profile = {
                "user_id": self.user_id,
                "linkedin_id": "test-linkedin-id-123",
                "first_name": "John",
                "last_name": "Farmer", 
                "email": self.test_data['main_user']['email'],
                "profile_picture": None,
                "headline": "Agricultural Professional",
                "summary": "Experienced farmer and consultant",
                "positions": [],
                "connected_at": datetime.utcnow()
            }
            
            # Insert or update the LinkedIn profile
            db.linkedin_profiles.update_one(
                {"user_id": self.user_id},
                {"$set": linkedin_profile},
                upsert=True
            )
            
            # Update user to indicate LinkedIn is connected
            db.users.update_one(
                {"id": self.user_id},
                {"$set": {"linkedin_connected": True, "linkedin_updated_at": datetime.utcnow()}}
            )
            
            mongo_client.close()
            return True
            
        except Exception as e:
            print(f"Failed to setup mock LinkedIn profile: {e}")
            return False

    def test_linkedin_learning_import_with_connection(self):
        """Test LinkedIn Learning certificate import with LinkedIn connection"""
        # First set up mock LinkedIn profile
        if not self.setup_mock_linkedin_profile():
            self.log_test("LinkedIn Learning Import (With Connection)", False, 
                        "Failed to setup mock LinkedIn profile")
            return False
        
        # Now try to import certificates
        success, response, status = self.make_request('POST', 'integrations/linkedin-learning/import-certificates', expected_status=200)
        
        if success:
            # Verify the response structure
            expected_keys = ['message', 'certificates_imported', 'skills_added']
            has_all_keys = all(key in response for key in expected_keys)
            
            if has_all_keys:
                certificates_count = response.get('certificates_imported', 0)
                skills_count = response.get('skills_added', 0)
                
                # Verify we got the expected 3 certificates
                if certificates_count == 3:
                    self.log_test("LinkedIn Learning Import (With Connection)", True, 
                                f"Imported {certificates_count} certificates, added {skills_count} skills")
                    self.test_data['linkedin_certificates_imported'] = True
                    return True
                else:
                    self.log_test("LinkedIn Learning Import (With Connection)", False, 
                                f"Expected 3 certificates, got {certificates_count}")
                    return False
            else:
                self.log_test("LinkedIn Learning Import (With Connection)", False, 
                            f"Missing expected keys in response: {response}")
                return False
        else:
            self.log_test("LinkedIn Learning Import (With Connection)", False, 
                        f"Status: {status}, Response: {response}")
            return False

    def test_linkedin_learning_import_certificates(self):
        """Test LinkedIn Learning certificate import functionality"""
        # Test both scenarios: without LinkedIn connection and with LinkedIn connection
        
        # First test without LinkedIn connection (should fail)
        success, response, status = self.make_request('POST', 'integrations/linkedin-learning/import-certificates', expected_status=404)
        
        if status == 404 and 'LinkedIn profile not connected' in str(response):
            self.log_test("LinkedIn Learning Import (No LinkedIn)", True, 
                        "Correctly returns 404 when LinkedIn not connected")
        else:
            self.log_test("LinkedIn Learning Import (No LinkedIn)", False, 
                        f"Unexpected response. Status: {status}, Response: {response}")
            return False
        
        # Now test with LinkedIn connection
        return self.test_linkedin_learning_import_with_connection()

    def test_linkedin_learning_get_certificates(self):
        """Test getting LinkedIn Learning certificates"""
        success, response, status = self.make_request('GET', 'integrations/linkedin-learning/certificates', expected_status=200)
        
        if success:
            # Verify response structure
            expected_keys = ['certificates', 'total']
            has_all_keys = all(key in response for key in expected_keys)
            
            if has_all_keys:
                certificates = response.get('certificates', [])
                total = response.get('total', 0)
                
                # If we previously imported certificates, verify they're here
                if self.test_data.get('linkedin_certificates_imported'):
                    if total == 3 and len(certificates) == 3:
                        # Verify certificate structure and content
                        expected_certificate_names = [
                            "Sustainable Agriculture Practices",
                            "Agricultural Technology and Innovation", 
                            "Organic Farming Certification Prep"
                        ]
                        
                        certificate_names = [cert.get('course_name', '') for cert in certificates]
                        
                        # Check if all expected certificates are present
                        all_certificates_present = all(name in certificate_names for name in expected_certificate_names)
                        
                        if all_certificates_present:
                            # Verify certificate structure
                            first_cert = certificates[0]
                            required_fields = ['certificate_id', 'course_name', 'course_url', 'completion_date', 'skills', 'verified']
                            has_required_fields = all(field in first_cert for field in required_fields)
                            
                            if has_required_fields:
                                self.log_test("LinkedIn Learning Get Certificates", True, 
                                            f"Retrieved {total} certificates with correct structure")
                                return True
                            else:
                                self.log_test("LinkedIn Learning Get Certificates", False, 
                                            f"Certificates missing required fields: {first_cert}")
                                return False
                        else:
                            self.log_test("LinkedIn Learning Get Certificates", False, 
                                        f"Missing expected certificates. Found: {certificate_names}")
                            return False
                    else:
                        self.log_test("LinkedIn Learning Get Certificates", False, 
                                    f"Expected 3 certificates, got {total}")
                        return False
                else:
                    # No certificates imported yet, but endpoint works
                    self.log_test("LinkedIn Learning Get Certificates", True, 
                                f"Retrieved {total} certificates (none imported yet)")
                    return True
            else:
                self.log_test("LinkedIn Learning Get Certificates", False, 
                            f"Missing expected keys in response: {response}")
                return False
        else:
            self.log_test("LinkedIn Learning Get Certificates", False, 
                        f"Status: {status}, Response: {response}")
            return False

    def test_linkedin_learning_certificate_skills_integration(self):
        """Test that LinkedIn Learning certificates properly integrate with user skills"""
        # Get the user's profile to check current skills
        success, response, status = self.make_request('GET', f'profiles/{self.user_id}', expected_status=200)
        
        if success and 'skills' in response:
            skills = response.get('skills', [])
            
            # Look for any existing LinkedIn Learning skills
            linkedin_skills = [skill for skill in skills if skill.get('verification_source') == 'linkedin_learning']
            
            if self.test_data.get('linkedin_certificates_imported'):
                # If certificates were imported, we should have LinkedIn Learning skills
                if linkedin_skills:
                    # Verify skill structure
                    first_linkedin_skill = linkedin_skills[0]
                    required_fields = ['id', 'name', 'category', 'verified', 'verification_source']
                    has_required_fields = all(field in first_linkedin_skill for field in required_fields)
                    
                    if has_required_fields:
                        self.log_test("LinkedIn Learning Skills Integration", True, 
                                    f"Found {len(linkedin_skills)} LinkedIn Learning skills with correct structure")
                        return True
                    else:
                        self.log_test("LinkedIn Learning Skills Integration", False, 
                                    f"LinkedIn Learning skills missing required fields: {first_linkedin_skill}")
                        return False
                else:
                    self.log_test("LinkedIn Learning Skills Integration", False, 
                                "No LinkedIn Learning skills found despite certificate import")
                    return False
            else:
                # No certificates imported, so no LinkedIn Learning skills expected
                if len(linkedin_skills) == 0:
                    self.log_test("LinkedIn Learning Skills Integration", True, 
                                "No LinkedIn Learning skills found (expected without import)")
                    return True
                else:
                    self.log_test("LinkedIn Learning Skills Integration", True, 
                                f"Found {len(linkedin_skills)} LinkedIn Learning skills from previous tests")
                    return True
        else:
            self.log_test("LinkedIn Learning Skills Integration", False, 
                        f"Could not retrieve profile. Status: {status}")
            return False

    # ========== PROJECT-BASED VALIDATION SYSTEM TESTS ==========
    
    def setup_additional_test_users(self):
        """Create additional test users for project collaboration testing"""
        additional_users = [
            {
                "role": "agronomist",
                "email": f"agronomist_{datetime.now().strftime('%H%M%S')}@test.com",
                "name": "Dr. Sarah Agronomist",
                "password": "TestPass123!"
            },
            {
                "role": "equipment_dealer", 
                "email": f"dealer_{datetime.now().strftime('%H%M%S')}@test.com",
                "name": "Mike Equipment",
                "password": "TestPass123!"
            }
        ]
        
        created_users = []
        for user_data in additional_users:
            success, response, status = self.make_request('POST', 'auth/register', user_data, 200)
            
            if success and 'access_token' in response:
                user_info = {
                    'user_id': response['user']['id'],
                    'name': response['user']['name'],
                    'email': response['user']['email'],
                    'role': response['user']['role'],
                    'token': response['access_token'],
                    'password': user_data['password']
                }
                created_users.append(user_info)
                self.log_test(f"Setup Additional User: {user_data['role']}", True, f"User ID: {response['user']['id']}")
            else:
                self.log_test(f"Setup Additional User: {user_data['role']}", False, f"Status: {status}")
        
        self.test_data['additional_users'] = created_users
        return len(created_users) >= 2

    def test_create_project_experience(self):
        """Test creating project experiences"""
        project_data = {
            "project_name": "Irrigation System Installation",
            "project_type": "irrigation",
            "description": "Installation of modern drip irrigation system across 200 acres of farmland to improve water efficiency and crop yields.",
            "location": "Cedar Falls, Iowa",
            "start_date": "2024-01-15T00:00:00Z",
            "end_date": "2024-03-30T00:00:00Z",
            "still_active": False,
            "skills_demonstrated": ["Irrigation Design", "Water Management", "Project Management", "Agricultural Engineering"],
            "collaborators": [],  # Will be populated by the API
            "project_results": "Successfully reduced water usage by 30% while increasing crop yield by 15%"
        }

        success, response, status = self.make_request('POST', 'projects', project_data, 200)
        
        if success and 'id' in response:
            self.test_data['project_id'] = response['id']
            self.log_test("Create Project Experience", True, f"Project ID: {response['id']}")
            
            # Verify the current user was added to collaborators
            if self.user_id in response.get('collaborators', []):
                self.log_test("Project Creator Auto-Added to Collaborators", True, "Creator added to collaborators list")
            else:
                self.log_test("Project Creator Auto-Added to Collaborators", False, "Creator not in collaborators list")
            
            return True
        else:
            self.log_test("Create Project Experience", False, f"Status: {status}, Response: {response}")
            return False

    def test_get_user_projects(self):
        """Test getting user's projects"""
        success, response, status = self.make_request('GET', 'projects', expected_status=200)
        
        if success and isinstance(response, list):
            # Should have at least the project we created
            if len(response) >= 1:
                project = response[0]
                required_fields = ['id', 'project_name', 'project_type', 'description', 'collaborators']
                has_required_fields = all(field in project for field in required_fields)
                
                if has_required_fields:
                    self.log_test("Get User Projects", True, f"Found {len(response)} projects with correct structure")
                    return True
                else:
                    self.log_test("Get User Projects", False, f"Project missing required fields: {project}")
                    return False
            else:
                self.log_test("Get User Projects", False, "No projects found")
                return False
        else:
            self.log_test("Get User Projects", False, f"Status: {status}")
            return False

    def test_get_specific_project(self):
        """Test getting a specific project"""
        if not self.test_data.get('project_id'):
            self.log_test("Get Specific Project", False, "No project ID available")
            return False

        project_id = self.test_data['project_id']
        success, response, status = self.make_request('GET', f'projects/{project_id}', expected_status=200)
        
        if success and 'project_name' in response:
            self.log_test("Get Specific Project", True, f"Project: {response['project_name']}")
            return True
        else:
            self.log_test("Get Specific Project", False, f"Status: {status}")
            return False

    def test_search_collaborators(self):
        """Test searching for potential collaborators"""
        # Test search without query (should return empty)
        success, response, status = self.make_request('GET', 'search/collaborators', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Search Collaborators (No Query)", True, f"Found {len(response)} users")
        else:
            self.log_test("Search Collaborators (No Query)", False, f"Status: {status}")
            return False

        # Test search with query
        success, response, status = self.make_request('GET', 'search/collaborators?q=agronomist', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Search Collaborators (With Query)", True, f"Found {len(response)} users matching 'agronomist'")
            
            # If we have results, verify structure
            if len(response) > 0:
                user = response[0]
                required_fields = ['user_id', 'name', 'email', 'role']
                has_required_fields = all(field in user for field in required_fields)
                
                if has_required_fields:
                    self.log_test("Collaborator Search Result Structure", True, "Search results have correct structure")
                    return True
                else:
                    self.log_test("Collaborator Search Result Structure", False, f"Missing fields in result: {user}")
                    return False
            else:
                self.log_test("Search Collaborators (With Query)", True, "No results found (expected if no matching users)")
                return True
        else:
            self.log_test("Search Collaborators (With Query)", False, f"Status: {status}")
            return False

    def test_invite_project_collaborator(self):
        """Test inviting collaborators to a project"""
        if not self.test_data.get('project_id'):
            self.log_test("Invite Project Collaborator", False, "No project ID available")
            return False

        if not self.test_data.get('additional_users') or len(self.test_data['additional_users']) == 0:
            self.log_test("Invite Project Collaborator", False, "No additional users available")
            return False

        project_id = self.test_data['project_id']
        collaborator_id = self.test_data['additional_users'][0]['user_id']
        
        # Use form data for the collaborator_user_id parameter
        success, response, status = self.make_request('POST', f'projects/{project_id}/invite-collaborator', 
                                                    {"collaborator_user_id": collaborator_id}, 200)
        
        if success and 'message' in response:
            self.log_test("Invite Project Collaborator", True, response['message'])
            self.test_data['invited_collaborator_id'] = collaborator_id
            return True
        else:
            self.log_test("Invite Project Collaborator", False, f"Status: {status}, Response: {response}")
            return False

    def test_create_project_validation(self):
        """Test creating project-based validation"""
        if not self.test_data.get('project_id'):
            self.log_test("Create Project Validation", False, "No project ID available")
            return False

        if not self.test_data.get('invited_collaborator_id'):
            self.log_test("Create Project Validation", False, "No invited collaborator available")
            return False

        validation_data = {
            "project_experience_id": self.test_data['project_id'],
            "validated_user_id": self.test_data['invited_collaborator_id'],
            "project_role": "Irrigation Specialist",
            "skills_validated": ["Irrigation Design", "Water Management", "Technical Problem Solving"],
            "collaboration_description": "Worked closely together on designing and implementing the drip irrigation system. Demonstrated excellent technical knowledge and problem-solving skills.",
            "performance_rating": 5,
            "would_work_again": True,
            "validation_evidence": "Photos of completed irrigation installation and water efficiency reports"
        }

        success, response, status = self.make_request('POST', 'projects/validate', validation_data, 200)
        
        if success and 'id' in response:
            self.test_data['project_validation_id'] = response['id']
            self.log_test("Create Project Validation", True, f"Validation ID: {response['id']}")
            return True
        else:
            self.log_test("Create Project Validation", False, f"Status: {status}, Response: {response}")
            return False

    def test_get_received_project_validations(self):
        """Test getting project validations received by user"""
        # Switch to the collaborator's token to check their received validations
        if not self.test_data.get('additional_users') or len(self.test_data['additional_users']) == 0:
            self.log_test("Get Received Project Validations", False, "No additional users available")
            return False

        # Store original token
        original_token = self.token
        
        # Switch to collaborator's token
        collaborator = self.test_data['additional_users'][0]
        self.token = collaborator['token']
        
        success, response, status = self.make_request('GET', 'projects/validations/received', expected_status=200)
        
        # Restore original token
        self.token = original_token
        
        if success and isinstance(response, list):
            self.log_test("Get Received Project Validations", True, f"Found {len(response)} received validations")
            
            # If we have validations, verify structure
            if len(response) > 0:
                validation = response[0]
                required_fields = ['id', 'project_experience_id', 'validator_id', 'validated_user_id', 'project_role', 'performance_rating']
                has_required_fields = all(field in validation for field in required_fields)
                
                if has_required_fields:
                    self.log_test("Project Validation Structure", True, "Validation has correct structure")
                    return True
                else:
                    self.log_test("Project Validation Structure", False, f"Missing fields: {validation}")
                    return False
            else:
                self.log_test("Get Received Project Validations", True, "No validations found (expected if none created)")
                return True
        else:
            self.log_test("Get Received Project Validations", False, f"Status: {status}")
            return False

    def test_get_given_project_validations(self):
        """Test getting project validations given by user"""
        success, response, status = self.make_request('GET', 'projects/validations/given', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Get Given Project Validations", True, f"Found {len(response)} given validations")
            return True
        else:
            self.log_test("Get Given Project Validations", False, f"Status: {status}")
            return False

    def test_approve_project_validation(self):
        """Test approving a project validation"""
        if not self.test_data.get('project_validation_id'):
            self.log_test("Approve Project Validation", False, "No project validation ID available")
            return False

        if not self.test_data.get('additional_users') or len(self.test_data['additional_users']) == 0:
            self.log_test("Approve Project Validation", False, "No additional users available")
            return False

        # Store original token
        original_token = self.token
        
        # Switch to collaborator's token (they need to approve their own validation)
        collaborator = self.test_data['additional_users'][0]
        self.token = collaborator['token']
        
        validation_id = self.test_data['project_validation_id']
        success, response, status = self.make_request('PUT', f'projects/validations/{validation_id}/approve', expected_status=200)
        
        # Restore original token
        self.token = original_token
        
        if success and 'message' in response:
            self.log_test("Approve Project Validation", True, response['message'])
            return True
        else:
            self.log_test("Approve Project Validation", False, f"Status: {status}, Response: {response}")
            return False

    def test_get_given_regular_validations(self):
        """Test getting regular validations given by user (enhanced endpoint)"""
        success, response, status = self.make_request('GET', 'validations/given', expected_status=200)
        
        if success and isinstance(response, list):
            self.log_test("Get Given Regular Validations", True, f"Found {len(response)} given regular validations")
            return True
        else:
            self.log_test("Get Given Regular Validations", False, f"Status: {status}")
            return False

    def run_project_validation_tests(self):
        """Run complete project-based validation system tests"""
        print("\n🏗️  Testing Project-Based Validation System...")
        print("-" * 50)
        
        # Setup additional users for collaboration testing
        if not self.setup_additional_test_users():
            print("❌ Failed to setup additional test users - skipping project tests")
            return False
        
        # Test complete project workflow
        success = True
        
        # Project Experience Tests
        if self.test_create_project_experience():
            self.test_get_user_projects()
            self.test_get_specific_project()
        else:
            success = False
        
        # Collaborator Management Tests
        self.test_search_collaborators()
        if not self.test_invite_project_collaborator():
            success = False
        
        # Project Validation Tests
        if self.test_create_project_validation():
            self.test_get_received_project_validations()
            self.test_get_given_project_validations()
            self.test_approve_project_validation()
        else:
            success = False
        
        # Enhanced Regular Validation Tests
        self.test_get_given_regular_validations()
        
        return success

    def run_all_tests(self):
        """Run all API tests"""
        print("🚀 Starting SharkNo Agricultural Professional Network API Tests")
        print("=" * 70)

        # Health check
        self.test_health_check()

        # Authentication tests
        if self.test_user_registration():
            self.test_user_login()
            self.test_get_current_user()

            # Profile tests
            if self.test_create_profile():
                self.test_get_profile()
                self.test_update_profile()

            # Service tests
            if self.test_create_service():
                self.test_get_services()
                self.test_get_services_by_type()
                self.test_get_single_service()

            # Validation tests
            if self.test_create_validation():
                self.test_get_validations()
                self.test_approve_validation()

            # Review tests
            if self.test_create_review():
                self.test_get_user_reviews()

            # Search tests
            self.test_search_profiles()

            # LinkedIn Learning Integration tests
            print("\n📚 Testing LinkedIn Learning Integration...")
            self.test_linkedin_learning_import_certificates()
            self.test_linkedin_learning_get_certificates()
            self.test_linkedin_learning_certificate_skills_integration()

            # Project-Based Validation System tests
            self.run_project_validation_tests()

        # Print final results
        print("\n" + "=" * 70)
        print(f"📊 Test Results: {self.tests_passed}/{self.tests_run} tests passed")
        
        if self.tests_passed == self.tests_run:
            print("🎉 All tests passed! API is working correctly.")
            return 0
        else:
            print(f"⚠️  {self.tests_run - self.tests_passed} tests failed.")
            return 1

def main():
    tester = SharkNoAPITester()
    return tester.run_all_tests()

if __name__ == "__main__":
    sys.exit(main())