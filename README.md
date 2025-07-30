# SharkNo Agricultural Professional Network

A comprehensive professional networking platform designed specifically for the agricultural industry. Connect farmers, consultants, equipment dealers, veterinarians, agronomists, and suppliers in one collaborative ecosystem.

## 🌾 Features

### Core Functionality
- **Professional Profiles** - Create detailed profiles showcasing agricultural expertise
- **Skill Validation** - Third-party validation system for professional capabilities
- **Service Marketplace** - List and discover agricultural services
- **Professional Search** - Find experts by location, skills, and specialization
- **Authentication System** - Secure JWT-based authentication

### Agricultural-Specific Features
- **Role-Based System** - Specialized for farmers, consultants, dealers, veterinarians, etc.
- **Service Categories** - Consultation, equipment rental, veterinary services, agronomic advice
- **Experience Levels** - Entry to expert level classifications
- **Geographic Targeting** - Location-based professional discovery

## 🛠️ Technology Stack

### Backend
- **FastAPI** - Modern, fast web framework for Python
- **MongoDB** - NoSQL database for flexible data storage
- **JWT Authentication** - Secure token-based authentication
- **Motor** - Async MongoDB driver
- **Pydantic** - Data validation and serialization
- **bcrypt** - Password hashing

### Frontend
- **React** - Modern JavaScript library for building user interfaces
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client for API calls

### Infrastructure
- **Docker** - Containerization support
- **Supervisor** - Process management
- **CORS** - Cross-origin resource sharing enabled

## 🚀 Getting Started

### Prerequisites
- Python 3.9+
- Node.js 16+
- MongoDB
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/sharkno-agricultural.git
   cd sharkno-agricultural
   ```

2. **Backend Setup**
   ```bash
   cd backend
   pip install -r requirements.txt
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   yarn install
   ```

4. **Environment Configuration**
   
   Create `.env` files in both backend and frontend directories:
   
   **Backend (.env):**
   ```env
   MONGO_URL="mongodb://localhost:27017"
   DB_NAME="sharkno_agricultural"
   JWT_SECRET="your-super-secret-jwt-key-change-this-in-production"
   ```
   
   **Frontend (.env):**
   ```env
   REACT_APP_BACKEND_URL=http://localhost:8001
   ```

5. **Start the Services**
   ```bash
   # Start MongoDB
   mongod
   
   # Start Backend (in backend directory)
   uvicorn server:app --host 0.0.0.0 --port 8001 --reload
   
   # Start Frontend (in frontend directory)
   yarn start
   ```

## 📱 User Roles and Capabilities

### Farmer 🌾
- Create farm profiles with crop specializations
- List farming services and equipment
- Validate other farmers' expertise
- Find agricultural consultants and suppliers

### Agricultural Consultant 🧑‍🌾
- Showcase consulting expertise and certifications
- Offer consultation services
- Build client testimonials through validations
- Connect with farmers and agricultural businesses

### Equipment Dealer 🚜
- List equipment rental and sales services
- Showcase inventory and specializations
- Connect with farmers needing equipment
- Build reputation through service validations

### Veterinarian 🐄
- Offer veterinary services for livestock
- List specializations and certifications
- Connect with farmers and livestock producers
- Build professional credibility

### Agronomist 🌱
- Provide soil, crop, and plant expertise
- Offer agronomic consultation services
- Validate agricultural practices
- Connect with farmers and agricultural businesses

### Supplier 📦
- List agricultural supplies and products
- Showcase product catalogs
- Connect with farmers and agricultural businesses
- Build supplier reputation

## 🔐 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user info

### Profiles
- `POST /api/profiles` - Create profile
- `GET /api/profiles/{user_id}` - Get profile
- `PUT /api/profiles/{user_id}` - Update profile

### Services
- `POST /api/services` - Create service
- `GET /api/services` - List services
- `GET /api/services/{service_id}` - Get service details

### Validations
- `POST /api/validations` - Create validation
- `GET /api/validations` - Get validations
- `PUT /api/validations/{validation_id}/approve` - Approve validation

### Search
- `GET /api/search/profiles` - Search professionals
- `GET /api/search/services` - Search services

## 🎯 Usage Examples

### Creating a Professional Profile
```javascript
// Frontend example
const profileData = {
  title: "Senior Agronomist",
  bio: "Specialist in sustainable farming practices...",
  profile_type: "individual",
  skills: [
    { name: "Soil Analysis", category: "Agronomy" },
    { name: "Crop Rotation", category: "Farming" }
  ],
  experience: [
    {
      position: "Lead Agronomist",
      company: "AgriCorp Solutions",
      start_date: "2020-01-01",
      description: "Led soil management projects..."
    }
  ]
};
```

### Validating Professional Skills
```javascript
// Validation example
const validation = {
  skill_id: "soil_analysis",
  validated_user_id: "user_123",
  description: "I've worked with John on multiple soil analysis projects. His expertise in pH testing and nutrient analysis is exceptional."
};
```

## 🔧 Development

### Project Structure
```
/app/
├── backend/                 # FastAPI backend
│   ├── server.py           # Main application
│   ├── requirements.txt    # Python dependencies
│   └── .env               # Environment variables
├── frontend/               # React frontend
│   ├── src/
│   │   ├── components/    # React components
│   │   ├── App.js         # Main app component
│   │   └── index.js       # Entry point
│   ├── package.json       # Node.js dependencies
│   └── .env              # Frontend environment
└── README.md             # This file
```

### Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Backend: Follow PEP 8 Python style guide
- Frontend: Use Prettier for code formatting
- Use meaningful commit messages
- Add comments for complex logic

## 📈 Future Enhancements

### Planned Features
- **Mobile App** - React Native application
- **Real-time Messaging** - WebSocket-based chat system
- **File Upload** - Document and image sharing
- **Reviews System** - Service and professional reviews
- **Payment Integration** - Secure payment processing
- **AI Recommendations** - ML-based professional matching
- **Integration with NYVA** - Connect with agricultural data platform

### Integration Possibilities
- **Weather APIs** - Farm weather data integration
- **Market Data** - Commodity price integrations
- **Geographic Services** - Enhanced location features
- **Agricultural IoT** - Connect with farm sensors and equipment

## 🤝 Professional Validation System

The core innovation of SharkNo is the third-party validation system:

1. **Skill Endorsements** - Professionals validate each other's capabilities
2. **Experience Verification** - Work history and project validations
3. **Reputation Building** - Trust metrics based on community validation
4. **Quality Assurance** - Peer review system for service quality

## 🌍 Agricultural Focus

Unlike generic professional networks, SharkNo is purpose-built for agriculture:

- **Industry-Specific Roles** - Tailored for agricultural professionals
- **Specialized Services** - Agricultural consultation, equipment, veterinary
- **Rural Connectivity** - Designed for agricultural communities
- **Seasonal Considerations** - Understands agricultural cycles and needs

## 🛡️ Security & Privacy

- **JWT Authentication** - Secure token-based authentication
- **Password Hashing** - bcrypt for secure password storage
- **CORS Protection** - Cross-origin resource sharing controls
- **Data Validation** - Pydantic models for data integrity
- **Environment Variables** - Secure configuration management

## 📞 Support

For support, feature requests, or contributions:
- Create an issue on GitHub
- Contact the development team
- Join our agricultural professional community

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built with modern web technologies
- Inspired by the agricultural community's need for professional networking
- Designed for the future of agricultural collaboration

---

**SharkNo Agricultural** - Connecting the agricultural world, one professional at a time. 🌾