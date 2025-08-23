#!/usr/bin/env python3

import requests
import json
import time

# Test entity search endpoint
base_url = "https://agriexperts.preview.emergentagent.com"
api_url = f"{base_url}/api"

# First register a user to get a token
user_data = {
    "role": "farmer",
    "email": f"test_entity_{int(time.time())}@test.com",
    "name": "Test User",
    "password": "TestPass123!"
}

print("Registering user...")
response = requests.post(f"{api_url}/auth/register", json=user_data)
print(f"Registration status: {response.status_code}")

if response.status_code == 200:
    token = response.json()['access_token']
    headers = {'Authorization': f'Bearer {token}'}
    
    # Test entity search
    print("\nTesting entity search...")
    
    test_queries = [
        "John Deere",
        "Syngenta", 
        "6R Series",
        "Tomate",
        "Finca",
        "Dr"
    ]
    
    for query in test_queries:
        print(f"\nSearching for: {query}")
        response = requests.get(f"{api_url}/search/entities?q={query}", headers=headers)
        print(f"Status: {response.status_code}")
        
        if response.status_code == 200:
            try:
                data = response.json()
                print(f"Response type: {type(data)}")
                print(f"Response length: {len(data) if isinstance(data, list) else 'Not a list'}")
                if isinstance(data, list) and len(data) > 0:
                    print(f"First result: {data[0]}")
                else:
                    print(f"Response: {data}")
            except Exception as e:
                print(f"JSON parse error: {e}")
                print(f"Raw response: {response.text[:200]}")
        else:
            print(f"Error response: {response.text}")

else:
    print(f"Registration failed: {response.text}")