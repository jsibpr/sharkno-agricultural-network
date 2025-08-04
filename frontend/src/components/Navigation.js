import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Navigation = ({ user, onLogout }) => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  const navItems = [
    { path: '/dashboard', label: 'Dashboard', icon: '🏠' },
    { path: '/profile', label: 'Profile', icon: '👤' },
    { path: '/projects', label: '🚜 Proyectos', icon: '🚜' },
    { path: '/services', label: 'Services', icon: '🛠️' },
    { path: '/validations', label: 'Validations', icon: '✅' },
    { path: '/search', label: 'Search', icon: '🔍' },
    { path: '/integrations', label: 'Integrations', icon: '🔗' }
  ];

  return (
    <nav className="bg-green-700 shadow-lg">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/dashboard" className="flex items-center space-x-2">
              <div className="h-8 w-8 bg-white rounded-full flex items-center justify-center">
                <span className="text-green-700 font-bold text-lg">🌾</span>
              </div>
              <span className="text-xl font-bold text-white">SharkNo Agricultural</span>
            </Link>
          </div>

          <div className="flex items-center space-x-1">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`px-3 py-2 rounded-md text-sm font-medium transition-colors flex items-center ${
                  isActive(item.path)
                    ? 'bg-green-600 text-white'
                    : 'text-green-100 hover:text-white hover:bg-green-600'
                }`}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </div>

          <div className="flex items-center space-x-4">
            <div className="text-sm text-green-100">
              <span className="font-medium">{user.name}</span>
            </div>
            <div className="text-xs text-green-200 bg-green-800 px-2 py-1 rounded">
              {user.role.replace('_', ' ').toUpperCase()}
            </div>
            <button
              onClick={onLogout}
              className="bg-red-600 text-white px-3 py-1 rounded-md text-sm font-medium hover:bg-red-700 transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;