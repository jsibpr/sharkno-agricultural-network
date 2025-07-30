import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Dashboard = ({ user }) => {
  const [stats, setStats] = useState({
    totalServices: 0,
    totalValidations: 0,
    totalReviews: 0,
    profileCompletion: 0
  });
  const [recentServices, setRecentServices] = useState([]);
  const [recentValidations, setRecentValidations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const token = localStorage.getItem('token');
      const headers = {
        'Authorization': `Bearer ${token}`
      };

      // Fetch services
      const servicesResponse = await fetch(`${API}/services`, { headers });
      const services = await servicesResponse.json();
      setRecentServices(services.slice(0, 3));

      // Fetch validations
      const validationsResponse = await fetch(`${API}/validations`, { headers });
      const validations = await validationsResponse.json();
      setRecentValidations(validations.slice(0, 3));

      // Update stats
      setStats({
        totalServices: services.length,
        totalValidations: validations.length,
        totalReviews: 0, // TODO: Implement reviews count
        profileCompletion: user.profile_completed ? 100 : 50
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon, color }) => (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center">
        <div className={`flex-shrink-0 p-3 rounded-full ${color}`}>
          <span className="text-2xl">{icon}</span>
        </div>
        <div className="ml-4">
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="text-2xl font-bold text-gray-900">{value}</p>
        </div>
      </div>
    </div>
  );

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

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-green-600 to-green-700 rounded-lg shadow-lg p-8 mb-8">
        <div className="flex items-center">
          <div className="text-6xl mr-6">
            {getRoleIcon(user.role)}
          </div>
          <div>
            <h1 className="text-3xl font-bold text-white">
              Welcome back, {user.name}!
            </h1>
            <p className="text-green-100 mt-2">
              {user.role.replace('_', ' ').toUpperCase()} • Agricultural Professional Network
            </p>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard
          title="My Services"
          value={stats.totalServices}
          icon="🛠️"
          color="bg-blue-100"
        />
        <StatCard
          title="Validations"
          value={stats.totalValidations}
          icon="✅"
          color="bg-green-100"
        />
        <StatCard
          title="Reviews"
          value={stats.totalReviews}
          icon="⭐"
          color="bg-yellow-100"
        />
        <StatCard
          title="Profile"
          value={`${stats.profileCompletion}%`}
          icon="👤"
          color="bg-purple-100"
        />
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow p-6 mb-8">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <a
            href="/profile"
            className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div className="flex items-center">
              <span className="text-2xl mr-3">👤</span>
              <div>
                <h3 className="font-medium">Complete Profile</h3>
                <p className="text-sm text-gray-500">Add your skills and experience</p>
              </div>
            </div>
          </a>
          <a
            href="/services"
            className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div className="flex items-center">
              <span className="text-2xl mr-3">🛠️</span>
              <div>
                <h3 className="font-medium">Create Service</h3>
                <p className="text-sm text-gray-500">Offer your agricultural services</p>
              </div>
            </div>
          </a>
          <a
            href="/search"
            className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div className="flex items-center">
              <span className="text-2xl mr-3">🔍</span>
              <div>
                <h3 className="font-medium">Find Professionals</h3>
                <p className="text-sm text-gray-500">Connect with others in agriculture</p>
              </div>
            </div>
          </a>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Recent Services */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Services</h2>
          {recentServices.length > 0 ? (
            <div className="space-y-4">
              {recentServices.map((service, index) => (
                <div key={index} className="border-l-4 border-green-500 pl-4">
                  <h3 className="font-medium">{service.title}</h3>
                  <p className="text-sm text-gray-600">{service.description}</p>
                  <p className="text-xs text-gray-500 mt-1">
                    {service.service_type.replace('_', ' ').toUpperCase()}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-500">No services yet</p>
              <a
                href="/services"
                className="text-green-600 hover:text-green-700 font-medium"
              >
                Create your first service
              </a>
            </div>
          )}
        </div>

        {/* Recent Validations */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Validations</h2>
          {recentValidations.length > 0 ? (
            <div className="space-y-4">
              {recentValidations.map((validation, index) => (
                <div key={index} className="border-l-4 border-blue-500 pl-4">
                  <h3 className="font-medium">Skill Validation</h3>
                  <p className="text-sm text-gray-600">{validation.description}</p>
                  <p className="text-xs text-gray-500 mt-1">
                    Status: {validation.status.toUpperCase()}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-500">No validations yet</p>
              <a
                href="/validations"
                className="text-green-600 hover:text-green-700 font-medium"
              >
                Get your skills validated
              </a>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;