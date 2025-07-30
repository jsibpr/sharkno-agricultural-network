import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Profile = ({ user }) => {
  const [profile, setProfile] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    bio: '',
    phone: '',
    website: '',
    city: '',
    state: '',
    country: '',
    profile_type: 'individual'
  });
  const [skills, setSkills] = useState([]);
  const [newSkill, setNewSkill] = useState({ name: '', category: '' });
  const [experience, setExperience] = useState([]);
  const [newExperience, setNewExperience] = useState({
    position: '',
    company: '',
    start_date: '',
    end_date: '',
    description: '',
    location: '',
    still_working: false
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/profiles/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const profileData = await response.json();
        setProfile(profileData);
        setFormData({
          title: profileData.title || '',
          bio: profileData.bio || '',
          phone: profileData.phone || '',
          website: profileData.website || '',
          city: profileData.address?.city || '',
          state: profileData.address?.state || '',
          country: profileData.address?.country || '',
          profile_type: profileData.profile_type || 'individual'
        });
        setSkills(profileData.skills || []);
        setExperience(profileData.experience || []);
      }
    } catch (error) {
      console.error('Error fetching profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const profileData = {
        ...formData,
        user_id: user.id,
        address: {
          city: formData.city,
          state: formData.state,
          country: formData.country
        },
        skills: skills,
        experience: experience
      };

      const url = profile ? `${API}/profiles/${user.id}` : `${API}/profiles`;
      const method = profile ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(profileData)
      });

      if (response.ok) {
        const updatedProfile = await response.json();
        setProfile(updatedProfile);
        setIsEditing(false);
      }
    } catch (error) {
      console.error('Error saving profile:', error);
    }
  };

  const addSkill = () => {
    if (newSkill.name.trim()) {
      setSkills([...skills, { ...newSkill, id: Date.now().toString() }]);
      setNewSkill({ name: '', category: '' });
    }
  };

  const removeSkill = (skillId) => {
    setSkills(skills.filter(skill => skill.id !== skillId));
  };

  const addExperience = () => {
    if (newExperience.position.trim() && newExperience.company.trim()) {
      setExperience([...experience, { 
        ...newExperience, 
        id: Date.now().toString(),
        start_date: new Date(newExperience.start_date).toISOString(),
        end_date: newExperience.end_date ? new Date(newExperience.end_date).toISOString() : null
      }]);
      setNewExperience({
        position: '',
        company: '',
        start_date: '',
        end_date: '',
        description: '',
        location: '',
        still_working: false
      });
    }
  };

  const removeExperience = (experienceId) => {
    setExperience(experience.filter(exp => exp.id !== experienceId));
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
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
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-green-600 to-green-700 px-6 py-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <div className="h-20 w-20 bg-white rounded-full flex items-center justify-center text-4xl">
                {user.role === 'farmer' ? '🌾' : 
                 user.role === 'consultant' ? '🧑‍🌾' : 
                 user.role === 'equipment_dealer' ? '🚜' : 
                 user.role === 'veterinarian' ? '🐄' : 
                 user.role === 'agronomist' ? '🌱' : 
                 user.role === 'supplier' ? '📦' : '👤'}
              </div>
              <div className="ml-6">
                <h1 className="text-3xl font-bold text-white">{user.name}</h1>
                <p className="text-green-100">{profile?.title || user.role.replace('_', ' ').toUpperCase()}</p>
                <p className="text-green-200 text-sm">{profile?.address?.city}, {profile?.address?.country}</p>
              </div>
            </div>
            <button
              onClick={() => setIsEditing(!isEditing)}
              className="bg-white text-green-600 px-4 py-2 rounded-md font-medium hover:bg-green-50 transition-colors"
            >
              {isEditing ? 'Cancel' : 'Edit Profile'}
            </button>
          </div>
        </div>

        {/* Profile Content */}
        <div className="p-6">
          {isEditing ? (
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Professional Title
                  </label>
                  <input
                    type="text"
                    value={formData.title}
                    onChange={(e) => setFormData({...formData, title: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="e.g., Senior Agronomist"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Profile Type
                  </label>
                  <select
                    value={formData.profile_type}
                    onChange={(e) => setFormData({...formData, profile_type: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  >
                    <option value="individual">Individual</option>
                    <option value="business">Business</option>
                    <option value="organization">Organization</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bio
                </label>
                <textarea
                  value={formData.bio}
                  onChange={(e) => setFormData({...formData, bio: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Tell us about your agricultural background and expertise..."
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Phone
                  </label>
                  <input
                    type="tel"
                    value={formData.phone}
                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Website
                  </label>
                  <input
                    type="url"
                    value={formData.website}
                    onChange={(e) => setFormData({...formData, website: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    City
                  </label>
                  <input
                    type="text"
                    value={formData.city}
                    onChange={(e) => setFormData({...formData, city: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    State
                  </label>
                  <input
                    type="text"
                    value={formData.state}
                    onChange={(e) => setFormData({...formData, state: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Country
                  </label>
                  <input
                    type="text"
                    value={formData.country}
                    onChange={(e) => setFormData({...formData, country: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
              </div>

              {/* Skills Section */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Skills</h3>
                <div className="space-y-2">
                  {skills.map((skill) => (
                    <div key={skill.id} className="flex items-center justify-between bg-gray-50 p-2 rounded">
                      <span>{skill.name} {skill.category && `(${skill.category})`}</span>
                      <button
                        type="button"
                        onClick={() => removeSkill(skill.id)}
                        className="text-red-600 hover:text-red-800"
                      >
                        Remove
                      </button>
                    </div>
                  ))}
                  <div className="flex space-x-2">
                    <input
                      type="text"
                      value={newSkill.name}
                      onChange={(e) => setNewSkill({...newSkill, name: e.target.value})}
                      placeholder="Skill name"
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                    <input
                      type="text"
                      value={newSkill.category}
                      onChange={(e) => setNewSkill({...newSkill, category: e.target.value})}
                      placeholder="Category"
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                    <button
                      type="button"
                      onClick={addSkill}
                      className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
                    >
                      Add
                    </button>
                  </div>
                </div>
              </div>

              {/* Experience Section */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Experience</h3>
                <div className="space-y-4">
                  {experience.map((exp) => (
                    <div key={exp.id} className="border border-gray-200 p-4 rounded-md">
                      <div className="flex items-center justify-between">
                        <div>
                          <h4 className="font-medium">{exp.position}</h4>
                          <p className="text-gray-600">{exp.company}</p>
                          <p className="text-sm text-gray-500">
                            {formatDate(exp.start_date)} - {exp.still_working ? 'Present' : formatDate(exp.end_date)}
                          </p>
                        </div>
                        <button
                          type="button"
                          onClick={() => removeExperience(exp.id)}
                          className="text-red-600 hover:text-red-800"
                        >
                          Remove
                        </button>
                      </div>
                    </div>
                  ))}
                  
                  <div className="border border-gray-200 p-4 rounded-md">
                    <h4 className="font-medium mb-2">Add Experience</h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <input
                        type="text"
                        value={newExperience.position}
                        onChange={(e) => setNewExperience({...newExperience, position: e.target.value})}
                        placeholder="Position"
                        className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                      <input
                        type="text"
                        value={newExperience.company}
                        onChange={(e) => setNewExperience({...newExperience, company: e.target.value})}
                        placeholder="Company"
                        className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                      <input
                        type="date"
                        value={newExperience.start_date}
                        onChange={(e) => setNewExperience({...newExperience, start_date: e.target.value})}
                        className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                      <input
                        type="date"
                        value={newExperience.end_date}
                        onChange={(e) => setNewExperience({...newExperience, end_date: e.target.value})}
                        disabled={newExperience.still_working}
                        className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                      />
                    </div>
                    <div className="mt-2">
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={newExperience.still_working}
                          onChange={(e) => setNewExperience({...newExperience, still_working: e.target.checked})}
                          className="mr-2"
                        />
                        I currently work here
                      </label>
                    </div>
                    <textarea
                      value={newExperience.description}
                      onChange={(e) => setNewExperience({...newExperience, description: e.target.value})}
                      placeholder="Description"
                      rows="2"
                      className="w-full mt-2 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    />
                    <button
                      type="button"
                      onClick={addExperience}
                      className="mt-2 bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
                    >
                      Add Experience
                    </button>
                  </div>
                </div>
              </div>

              <div className="flex justify-end">
                <button
                  type="submit"
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
                >
                  Save Profile
                </button>
              </div>
            </form>
          ) : (
            <div className="space-y-6">
              {/* Bio */}
              {profile?.bio && (
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">About</h3>
                  <p className="text-gray-600">{profile.bio}</p>
                </div>
              )}

              {/* Contact Info */}
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">Contact Information</h3>
                <div className="space-y-2 text-sm">
                  <p><span className="font-medium">Email:</span> {user.email}</p>
                  {profile?.phone && <p><span className="font-medium">Phone:</span> {profile.phone}</p>}
                  {profile?.website && <p><span className="font-medium">Website:</span> <a href={profile.website} target="_blank" rel="noopener noreferrer" className="text-green-600 hover:text-green-800">{profile.website}</a></p>}
                </div>
              </div>

              {/* Skills */}
              {skills.length > 0 && (
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">Skills</h3>
                  <div className="flex flex-wrap gap-2">
                    {skills.map((skill) => (
                      <span key={skill.id} className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm">
                        {skill.name}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {/* Experience */}
              {experience.length > 0 && (
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">Experience</h3>
                  <div className="space-y-4">
                    {experience.map((exp) => (
                      <div key={exp.id} className="border-l-4 border-green-500 pl-4">
                        <h4 className="font-medium">{exp.position}</h4>
                        <p className="text-gray-600">{exp.company}</p>
                        <p className="text-sm text-gray-500">
                          {formatDate(exp.start_date)} - {exp.still_working ? 'Present' : formatDate(exp.end_date)}
                        </p>
                        {exp.description && <p className="text-gray-600 mt-2">{exp.description}</p>}
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;