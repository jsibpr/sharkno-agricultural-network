import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Services = ({ user }) => {
  const [services, setServices] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    service_type: 'consultation',
    price_min: '',
    price_max: '',
    currency: 'USD',
    location: '',
    experience_level: 'intermediate',
    skills_required: [],
    availability: ''
  });
  const [newSkill, setNewSkill] = useState('');
  const [loading, setLoading] = useState(true);

  const serviceTypes = [
    { value: 'consultation', label: 'Agricultural Consultation' },
    { value: 'equipment_rental', label: 'Equipment Rental' },
    { value: 'veterinary', label: 'Veterinary Services' },
    { value: 'agronomic_advice', label: 'Agronomic Advice' },
    { value: 'crop_protection', label: 'Crop Protection' },
    { value: 'soil_analysis', label: 'Soil Analysis' },
    { value: 'irrigation', label: 'Irrigation Services' },
    { value: 'harvesting', label: 'Harvesting Services' }
  ];

  const experienceLevels = [
    { value: 'entry', label: 'Entry Level' },
    { value: 'intermediate', label: 'Intermediate' },
    { value: 'advanced', label: 'Advanced' },
    { value: 'expert', label: 'Expert' }
  ];

  useEffect(() => {
    fetchServices();
  }, []);

  const fetchServices = async () => {
    try {
      const response = await fetch(`${API}/services`);
      const data = await response.json();
      setServices(data);
    } catch (error) {
      console.error('Error fetching services:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/services`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...formData,
          price_min: formData.price_min ? parseFloat(formData.price_min) : null,
          price_max: formData.price_max ? parseFloat(formData.price_max) : null
        })
      });

      if (response.ok) {
        await fetchServices();
        setIsCreating(false);
        setFormData({
          title: '',
          description: '',
          service_type: 'consultation',
          price_min: '',
          price_max: '',
          currency: 'USD',
          location: '',
          experience_level: 'intermediate',
          skills_required: [],
          availability: ''
        });
      }
    } catch (error) {
      console.error('Error creating service:', error);
    }
  };

  const addSkill = () => {
    if (newSkill.trim()) {
      setFormData({
        ...formData,
        skills_required: [...formData.skills_required, newSkill.trim()]
      });
      setNewSkill('');
    }
  };

  const removeSkill = (skillToRemove) => {
    setFormData({
      ...formData,
      skills_required: formData.skills_required.filter(skill => skill !== skillToRemove)
    });
  };

  const formatServiceType = (type) => {
    return type.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  };

  const formatPrice = (min, max, currency) => {
    if (!min && !max) return 'Price on request';
    if (min && max) return `${currency} ${min} - ${max}`;
    if (min) return `From ${currency} ${min}`;
    if (max) return `Up to ${currency} ${max}`;
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

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Agricultural Services</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
        >
          Create Service
        </button>
      </div>

      {/* Create Service Modal */}
      {isCreating && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-screen overflow-y-auto">
            <h2 className="text-2xl font-bold mb-4">Create New Service</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Service Title
                </label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({...formData, title: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Service Type
                  </label>
                  <select
                    value={formData.service_type}
                    onChange={(e) => setFormData({...formData, service_type: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  >
                    {serviceTypes.map(type => (
                      <option key={type.value} value={type.value}>
                        {type.label}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Experience Level
                  </label>
                  <select
                    value={formData.experience_level}
                    onChange={(e) => setFormData({...formData, experience_level: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  >
                    {experienceLevels.map(level => (
                      <option key={level.value} value={level.value}>
                        {level.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Price From
                  </label>
                  <input
                    type="number"
                    value={formData.price_min}
                    onChange={(e) => setFormData({...formData, price_min: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="0"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Price To
                  </label>
                  <input
                    type="number"
                    value={formData.price_max}
                    onChange={(e) => setFormData({...formData, price_max: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="0"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Currency
                  </label>
                  <select
                    value={formData.currency}
                    onChange={(e) => setFormData({...formData, currency: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  >
                    <option value="USD">USD</option>
                    <option value="EUR">EUR</option>
                    <option value="GBP">GBP</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Location
                </label>
                <input
                  type="text"
                  value={formData.location}
                  onChange={(e) => setFormData({...formData, location: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="City, State, Country"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Skills Required
                </label>
                <div className="flex flex-wrap gap-2 mb-2">
                  {formData.skills_required.map((skill, index) => (
                    <span key={index} className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm flex items-center">
                      {skill}
                      <button
                        type="button"
                        onClick={() => removeSkill(skill)}
                        className="ml-2 text-green-600 hover:text-green-800"
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>
                <div className="flex space-x-2">
                  <input
                    type="text"
                    value={newSkill}
                    onChange={(e) => setNewSkill(e.target.value)}
                    placeholder="Add skill"
                    className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                  <button
                    type="button"
                    onClick={addSkill}
                    className="bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700"
                  >
                    Add
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Availability
                </label>
                <input
                  type="text"
                  value={formData.availability}
                  onChange={(e) => setFormData({...formData, availability: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="e.g., Monday-Friday, 9 AM - 5 PM"
                />
              </div>

              <div className="flex justify-end space-x-4">
                <button
                  type="button"
                  onClick={() => setIsCreating(false)}
                  className="px-4 py-2 text-gray-600 hover:text-gray-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
                >
                  Create Service
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Services Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {services.map((service) => (
          <div key={service.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="text-3xl">
                  {getServiceIcon(service.service_type)}
                </div>
                <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full">
                  {formatServiceType(service.service_type)}
                </span>
              </div>
              
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                {service.title}
              </h3>
              
              <p className="text-gray-600 mb-4 line-clamp-3">
                {service.description}
              </p>
              
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">Price:</span>
                  <span className="font-medium">
                    {formatPrice(service.price_min, service.price_max, service.currency)}
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
              
              {service.skills_required && service.skills_required.length > 0 && (
                <div className="mt-4">
                  <p className="text-sm text-gray-500 mb-2">Skills Required:</p>
                  <div className="flex flex-wrap gap-1">
                    {service.skills_required.slice(0, 3).map((skill, index) => (
                      <span key={index} className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded">
                        {skill}
                      </span>
                    ))}
                    {service.skills_required.length > 3 && (
                      <span className="text-xs text-gray-500">
                        +{service.skills_required.length - 3} more
                      </span>
                    )}
                  </div>
                </div>
              )}
              
              <div className="mt-6 flex justify-between items-center">
                <button className="bg-green-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-green-700 transition-colors">
                  Contact Provider
                </button>
                <div className="text-xs text-gray-500">
                  {new Date(service.created_at).toLocaleDateString()}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {services.length === 0 && (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🛠️</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No services available</h3>
          <p className="text-gray-600">Be the first to create an agricultural service!</p>
        </div>
      )}
    </div>
  );
};

export default Services;