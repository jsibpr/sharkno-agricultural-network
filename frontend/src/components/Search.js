import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Search = ({ user }) => {
  const [searchType, setSearchType] = useState('profiles');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [filters, setFilters] = useState({
    role: '',
    location: '',
    serviceType: ''
  });
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  const roleOptions = [
    { value: '', label: 'All Roles' },
    { value: 'farmer', label: 'Farmer' },
    { value: 'consultant', label: 'Agricultural Consultant' },
    { value: 'equipment_dealer', label: 'Equipment Dealer' },
    { value: 'veterinarian', label: 'Veterinarian' },
    { value: 'agronomist', label: 'Agronomist' },
    { value: 'supplier', label: 'Supplier' }
  ];

  const serviceTypes = [
    { value: '', label: 'All Services' },
    { value: 'consultation', label: 'Agricultural Consultation' },
    { value: 'equipment_rental', label: 'Equipment Rental' },
    { value: 'veterinary', label: 'Veterinary Services' },
    { value: 'agronomic_advice', label: 'Agronomic Advice' },
    { value: 'crop_protection', label: 'Crop Protection' },
    { value: 'soil_analysis', label: 'Soil Analysis' },
    { value: 'irrigation', label: 'Irrigation Services' },
    { value: 'harvesting', label: 'Harvesting Services' }
  ];

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setHasSearched(true);

    try {
      const token = localStorage.getItem('token');
      let url = `${API}/search/${searchType}`;
      const params = new URLSearchParams();
      
      if (searchQuery.trim()) {
        params.append('q', searchQuery);
      }
      
      if (filters.role) {
        params.append('role', filters.role);
      }
      
      if (filters.location) {
        params.append('location', filters.location);
      }
      
      if (filters.serviceType) {
        params.append('service_type', filters.serviceType);
      }

      if (params.toString()) {
        url += `?${params.toString()}`;
      }

      const response = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      const data = await response.json();
      setSearchResults(data);
    } catch (error) {
      console.error('Error searching:', error);
    } finally {
      setLoading(false);
    }
  };

  const getRoleIcon = (role) => {
    const icons = {
      farmer: '🌾',
      consultant: '🧑‍🌾',
      equipment_dealer: '🚜',
      veterinarian: '🐄',
      agronomist: '🌱',
      supplier: '📦'
    };
    return icons[role] || '👤';
  };

  const getServiceIcon = (type) => {
    const icons = {
      consultation: '🤝',
      equipment_rental: '🚜',
      veterinary: '🐄',
      agronomic_advice: '🌱',
      crop_protection: '🛡️',
      soil_analysis: '🔬',
      irrigation: '💧',
      harvesting: '🌾'
    };
    return icons[type] || '🛠️';
  };

  const formatServiceType = (type) => {
    return type.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  };

  const ProfileCard = ({ profile }) => (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex items-center mb-4">
        <div className="text-4xl mr-4">
          {/* We would need to get the user role from somewhere */}
          👤
        </div>
        <div>
          <h3 className="text-xl font-semibold text-gray-900">{profile.title}</h3>
          <p className="text-gray-600">{profile.profile_type}</p>
          {profile.address && (
            <p className="text-sm text-gray-500">
              {profile.address.city}, {profile.address.country}
            </p>
          )}
        </div>
      </div>

      {profile.bio && (
        <p className="text-gray-600 mb-4 line-clamp-3">{profile.bio}</p>
      )}

      {profile.skills && profile.skills.length > 0 && (
        <div className="mb-4">
          <p className="text-sm font-medium text-gray-700 mb-2">Skills:</p>
          <div className="flex flex-wrap gap-2">
            {profile.skills.slice(0, 3).map((skill, index) => (
              <span key={index} className="bg-green-100 text-green-800 px-2 py-1 rounded text-sm">
                {skill.name}
              </span>
            ))}
            {profile.skills.length > 3 && (
              <span className="text-sm text-gray-500">+{profile.skills.length - 3} more</span>
            )}
          </div>
        </div>
      )}

      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <div className="flex items-center">
            <span className="text-yellow-400">⭐</span>
            <span className="text-sm text-gray-600 ml-1">
              {profile.rating || 0} ({profile.total_validations || 0} validations)
            </span>
          </div>
        </div>
        <button className="bg-green-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-green-700 transition-colors">
          View Profile
        </button>
      </div>
    </div>
  );

  const ServiceCard = ({ service }) => (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex items-center justify-between mb-4">
        <div className="text-3xl">
          {getServiceIcon(service.service_type)}
        </div>
        <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full">
          {formatServiceType(service.service_type)}
        </span>
      </div>
      
      <h3 className="text-xl font-semibold text-gray-900 mb-2">{service.title}</h3>
      <p className="text-gray-600 mb-4 line-clamp-3">{service.description}</p>
      
      <div className="space-y-2 text-sm">
        <div className="flex justify-between">
          <span className="text-gray-500">Price:</span>
          <span className="font-medium">
            {service.price_min && service.price_max
              ? `${service.currency} ${service.price_min} - ${service.price_max}`
              : service.price_min
              ? `From ${service.currency} ${service.price_min}`
              : 'Price on request'}
          </span>
        </div>
        
        {service.location && (
          <div className="flex justify-between">
            <span className="text-gray-500">Location:</span>
            <span>{service.location}</span>
          </div>
        )}
        
        <div className="flex justify-between">
          <span className="text-gray-500">Experience:</span>
          <span className="capitalize">{service.experience_level}</span>
        </div>
      </div>
      
      <div className="mt-4 flex justify-between items-center">
        <button className="bg-green-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-green-700 transition-colors">
          Contact Provider
        </button>
        <div className="text-xs text-gray-500">
          {new Date(service.created_at).toLocaleDateString()}
        </div>
      </div>
    </div>
  );

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Search Agricultural Network</h1>

      {/* Search Form */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-8">
        <form onSubmit={handleSearch} className="space-y-4">
          {/* Search Type Tabs */}
          <div className="flex space-x-4 mb-6">
            <button
              type="button"
              onClick={() => setSearchType('profiles')}
              className={`px-4 py-2 rounded-md font-medium transition-colors ${
                searchType === 'profiles'
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Search Professionals
            </button>
            <button
              type="button"
              onClick={() => setSearchType('services')}
              className={`px-4 py-2 rounded-md font-medium transition-colors ${
                searchType === 'services'
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Search Services
            </button>
          </div>

          {/* Search Input */}
          <div>
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder={
                searchType === 'profiles'
                  ? 'Search by name, skills, or expertise...'
                  : 'Search services by title, description...'
              }
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 text-lg"
            />
          </div>

          {/* Filters */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {searchType === 'profiles' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Role
                </label>
                <select
                  value={filters.role}
                  onChange={(e) => setFilters({...filters, role: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {roleOptions.map(option => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {searchType === 'services' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Service Type
                </label>
                <select
                  value={filters.serviceType}
                  onChange={(e) => setFilters({...filters, serviceType: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {serviceTypes.map(option => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Location
              </label>
              <input
                type="text"
                value={filters.location}
                onChange={(e) => setFilters({...filters, location: e.target.value})}
                placeholder="City, State, Country"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          </div>

          {/* Search Button */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 text-white py-3 px-4 rounded-md font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
          >
            {loading ? 'Searching...' : `Search ${searchType === 'profiles' ? 'Professionals' : 'Services'}`}
          </button>
        </form>
      </div>

      {/* Results */}
      {hasSearched && (
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">
              {searchType === 'profiles' ? 'Professionals' : 'Services'} Found: {searchResults.length}
            </h2>
            {searchQuery && (
              <p className="text-gray-600">
                Results for "{searchQuery}"
              </p>
            )}
          </div>

          {loading ? (
            <div className="flex items-center justify-center h-64">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {searchResults.map((result, index) => (
                <div key={index}>
                  {searchType === 'profiles' ? (
                    <ProfileCard profile={result} />
                  ) : (
                    <ServiceCard service={result} />
                  )}
                </div>
              ))}
            </div>
          )}

          {!loading && searchResults.length === 0 && (
            <div className="text-center py-12">
              <div className="text-6xl mb-4">🔍</div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                No {searchType === 'profiles' ? 'professionals' : 'services'} found
              </h3>
              <p className="text-gray-600">
                Try adjusting your search terms or filters
              </p>
            </div>
          )}
        </div>
      )}

      {/* Default State */}
      {!hasSearched && (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🌾</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            Discover the Agricultural Community
          </h3>
          <p className="text-gray-600 mb-8">
            Find professionals, services, and opportunities in agriculture
          </p>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-2xl mx-auto">
            <div className="text-center">
              <div className="text-4xl mb-3">👥</div>
              <h4 className="font-medium text-gray-900 mb-2">Find Professionals</h4>
              <p className="text-sm text-gray-600">
                Connect with farmers, consultants, veterinarians, and agricultural experts
              </p>
            </div>
            
            <div className="text-center">
              <div className="text-4xl mb-3">🛠️</div>
              <h4 className="font-medium text-gray-900 mb-2">Discover Services</h4>
              <p className="text-sm text-gray-600">
                Find agricultural services, equipment rental, and professional consultations
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Search;