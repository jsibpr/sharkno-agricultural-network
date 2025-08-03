import requests
import sys
import json
from datetime import datetime, timedelta
from typing import Dict, Any

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

    def test_linkedin_learning_import_certificates(self):
        """Test LinkedIn Learning certificate import functionality"""
        # First, we need to create a mock LinkedIn profile connection
        # In the real implementation, this would be done through OAuth flow
        # For testing, we'll simulate having a LinkedIn profile connected
        
        # Create a mock LinkedIn profile entry in the database
        linkedin_profile_data = {
            "user_id": self.user_id,
            "linkedin_id": "test-linkedin-id-123",
            "first_name": "John",
            "last_name": "Farmer",
            "email": self.test_data['main_user']['email'],
            "connected_at": datetime.utcnow().isoformat()
        }
        
        # We'll simulate the LinkedIn connection by calling the import endpoint
        # The endpoint should work with mock data even without actual LinkedIn connection
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
                    self.log_test("LinkedIn Learning Import Certificates", True, 
                                f"Imported {certificates_count} certificates, added {skills_count} skills")
                    self.test_data['linkedin_certificates_imported'] = True
                    return True
                else:
                    self.log_test("LinkedIn Learning Import Certificates", False, 
                                f"Expected 3 certificates, got {certificates_count}")
                    return False
            else:
                self.log_test("LinkedIn Learning Import Certificates", False, 
                            f"Missing expected keys in response: {response}")
                return False
        else:
            # Check if it's a LinkedIn not connected error (expected behavior)
            if status == 404 and 'LinkedIn profile not connected' in str(response):
                # This is expected behavior - let's create a mock LinkedIn profile first
                self.log_test("LinkedIn Learning Import Certificates", False, 
                            "LinkedIn profile not connected (expected for testing)")
                return self.test_linkedin_learning_with_mock_profile()
            else:
                self.log_test("LinkedIn Learning Import Certificates", False, 
                            f"Status: {status}, Response: {response}")
                return False

    def test_linkedin_learning_with_mock_profile(self):
        """Test LinkedIn Learning with mock profile setup"""
        # For testing purposes, we'll directly test the certificate retrieval
        # after simulating the import process
        
        # Try to get certificates (should be empty initially)
        success, response, status = self.make_request('GET', 'integrations/linkedin-learning/certificates', expected_status=200)
        
        if success:
            certificates = response.get('certificates', [])
            total = response.get('total', 0)
            
            self.log_test("LinkedIn Learning Get Certificates (Initial)", True, 
                        f"Found {total} certificates initially")
            
            # Since we can't import without LinkedIn connection, we'll test the endpoint structure
            return True
        else:
            self.log_test("LinkedIn Learning Get Certificates (Initial)", False, 
                        f"Status: {status}, Response: {response}")
            return False

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
        if not self.test_data.get('linkedin_certificates_imported'):
            self.log_test("LinkedIn Learning Skills Integration", False, 
                        "No certificates imported to test skills integration")
            return False
        
        # Get the user's profile to check if skills were added
        success, response, status = self.make_request('GET', f'profiles/{self.user_id}', expected_status=200)
        
        if success and 'skills' in response:
            skills = response.get('skills', [])
            
            # Look for skills that should have been added from LinkedIn Learning certificates
            expected_skills = [
                "Sustainable Farming", "Crop Rotation", "Soil Management",
                "AgTech", "Precision Agriculture", "Farm Management Software",
                "Organic Farming", "USDA Organic Standards", "Pest Management"
            ]
            
            linkedin_skills = [skill for skill in skills if skill.get('verification_source') == 'linkedin_learning']
            linkedin_skill_names = [skill.get('name', '') for skill in linkedin_skills]
            
            # Check if any LinkedIn Learning skills were added
            if linkedin_skills:
                skills_found = [skill for skill in expected_skills if skill in linkedin_skill_names]
                
                if skills_found:
                    self.log_test("LinkedIn Learning Skills Integration", True, 
                                f"Found {len(skills_found)} LinkedIn Learning skills in profile")
                    return True
                else:
                    self.log_test("LinkedIn Learning Skills Integration", False, 
                                f"No expected LinkedIn Learning skills found. Available: {linkedin_skill_names}")
                    return False
            else:
                self.log_test("LinkedIn Learning Skills Integration", False, 
                            "No LinkedIn Learning skills found in profile")
                return False
        else:
            self.log_test("LinkedIn Learning Skills Integration", False, 
                        f"Could not retrieve profile. Status: {status}")
            return False

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