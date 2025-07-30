import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Validations = ({ user }) => {
  const [validations, setValidations] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [formData, setFormData] = useState({
    skill_id: '',
    validated_user_id: '',
    description: ''
  });
  const [searchUser, setSearchUser] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchLoading, setSearchLoading] = useState(false);

  useEffect(() => {
    fetchValidations();
  }, []);

  const fetchValidations = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setValidations(data);
    } catch (error) {
      console.error('Error fetching validations:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchUsers = async (query) => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }

    setSearchLoading(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/search/profiles?q=${encodeURIComponent(query)}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setSearchResults(data);
    } catch (error) {
      console.error('Error searching users:', error);
    } finally {
      setSearchLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        await fetchValidations();
        setIsCreating(false);
        setFormData({
          skill_id: '',
          validated_user_id: '',
          description: ''
        });
        setSearchUser('');
        setSearchResults([]);
      }
    } catch (error) {
      console.error('Error creating validation:', error);
    }
  };

  const handleApprove = async (validationId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations/${validationId}/approve`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        await fetchValidations();
      }
    } catch (error) {
      console.error('Error approving validation:', error);
    }
  };

  const selectUser = (selectedUser) => {
    setFormData({
      ...formData,
      validated_user_id: selectedUser.user_id
    });
    setSearchUser(selectedUser.title || 'Selected User');
    setSearchResults([]);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'approved':
        return 'bg-green-100 text-green-800';
      case 'rejected':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
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
        <h1 className="text-3xl font-bold text-gray-900">Skill Validations</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
        >
          Validate Someone
        </button>
      </div>

      {/* Create Validation Modal */}
      {isCreating && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-2xl font-bold mb-4">Create Skill Validation</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Search User to Validate
                </label>
                <input
                  type="text"
                  value={searchUser}
                  onChange={(e) => {
                    setSearchUser(e.target.value);
                    searchUsers(e.target.value);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Search by name or skills..."
                />
                
                {searchLoading && (
                  <div className="mt-2 text-sm text-gray-500">Searching...</div>
                )}
                
                {searchResults.length > 0 && (
                  <div className="mt-2 max-h-48 overflow-y-auto border border-gray-300 rounded-md">
                    {searchResults.map((result) => (
                      <div
                        key={result.user_id}
                        onClick={() => selectUser(result)}
                        className="p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-200 last:border-b-0"
                      >
                        <div className="font-medium">{result.title}</div>
                        <div className="text-sm text-gray-600">{result.bio}</div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Skill ID
                </label>
                <input
                  type="text"
                  value={formData.skill_id}
                  onChange={(e) => setFormData({...formData, skill_id: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Enter skill identifier"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Validation Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Describe how you've witnessed this person's skills..."
                  required
                />
              </div>

              <div className="flex justify-end space-x-4">
                <button
                  type="button"
                  onClick={() => {
                    setIsCreating(false);
                    setFormData({
                      skill_id: '',
                      validated_user_id: '',
                      description: ''
                    });
                    setSearchUser('');
                    setSearchResults([]);
                  }}
                  className="px-4 py-2 text-gray-600 hover:text-gray-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!formData.validated_user_id}
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
                >
                  Create Validation
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Explanation */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold text-blue-900 mb-2">How Skill Validation Works</h2>
        <div className="text-blue-700 space-y-2">
          <p>• <strong>Validate Others:</strong> Endorse the skills of agricultural professionals you've worked with</p>
          <p>• <strong>Get Validated:</strong> Receive endorsements from colleagues and clients</p>
          <p>• <strong>Build Trust:</strong> Third-party validations increase credibility in the agricultural community</p>
          <p>• <strong>Approve Validations:</strong> You can approve or reject validations received for your skills</p>
        </div>
      </div>

      {/* Validations List */}
      <div className="space-y-6">
        {validations.length > 0 ? (
          validations.map((validation) => (
            <div key={validation.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center space-x-3 mb-3">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(validation.status)}`}>
                      {validation.status.toUpperCase()}
                    </span>
                    <span className="text-sm text-gray-500">
                      {formatDate(validation.created_at)}
                    </span>
                  </div>
                  
                  <div className="mb-4">
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      Skill: {validation.skill_id}
                    </h3>
                    <p className="text-gray-600">
                      {validation.description}
                    </p>
                  </div>
                  
                  <div className="text-sm text-gray-500">
                    <p>Validator ID: {validation.validator_id}</p>
                    <p>Validated User ID: {validation.validated_user_id}</p>
                  </div>
                </div>
                
                {validation.status === 'pending' && validation.validated_user_id === user.id && (
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleApprove(validation.id)}
                      className="bg-green-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-green-700 transition-colors"
                    >
                      Approve
                    </button>
                    <button className="bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-red-700 transition-colors">
                      Reject
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">✅</div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No validations yet</h3>
            <p className="text-gray-600 mb-4">
              Start building your professional credibility by validating others' skills
            </p>
            <button
              onClick={() => setIsCreating(true)}
              className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
            >
              Validate Someone's Skills
            </button>
          </div>
        )}
      </div>

      {/* Tips */}
      <div className="mt-12 bg-green-50 border border-green-200 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-green-900 mb-3">Tips for Effective Validations</h3>
        <ul className="text-green-700 space-y-2">
          <li>• Be specific about the skills you're validating</li>
          <li>• Provide concrete examples of how you've seen these skills in action</li>
          <li>• Only validate skills you've personally witnessed or worked with</li>
          <li>• Keep validations professional and factual</li>
          <li>• The more detailed your validation, the more valuable it becomes</li>
        </ul>
      </div>
    </div>
  );
};

export default Validations;