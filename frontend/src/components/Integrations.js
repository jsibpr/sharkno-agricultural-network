import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Integrations = ({ user }) => {
  const [linkedInConnected, setLinkedInConnected] = useState(false);
  const [linkedInProfile, setLinkedInProfile] = useState(null);
  const [linkedInCertificates, setLinkedInCertificates] = useState([]);
  const [syncing, setSyncing] = useState(false);
  const [importingCerts, setImportingCerts] = useState(false);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [searchParams] = useSearchParams();

  useEffect(() => {
    checkLinkedInConnection();
    checkURLParams();
  }, []);

  const checkURLParams = () => {
    const linkedInStatus = searchParams.get('linkedin');
    if (linkedInStatus === 'connected') {
      setMessage('✅ LinkedIn profile connected successfully!');
      setTimeout(() => setMessage(''), 5000);
      checkLinkedInConnection();
    } else if (linkedInStatus === 'error') {
      setMessage('❌ Error connecting LinkedIn profile. Please try again.');
      setTimeout(() => setMessage(''), 5000);
    }
  };

  const checkLinkedInConnection = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/integrations/linkedin/profile`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setLinkedInConnected(true);
        setLinkedInProfile(data);
        
        // Also fetch LinkedIn Learning certificates
        const certResponse = await fetch(`${API}/integrations/linkedin-learning/certificates`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (certResponse.ok) {
          const certData = await certResponse.json();
          setLinkedInCertificates(certData.certificates || []);
        }
      } else {
        setLinkedInConnected(false);
      }
    } catch (error) {
      console.error('Error checking LinkedIn connection:', error);
      setLinkedInConnected(false);
    } finally {
      setLoading(false);
    }
  };

  const connectLinkedIn = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/auth/linkedin`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        // Redirect to LinkedIn OAuth
        window.location.href = data.auth_url;
      } else {
        setMessage('❌ Error initiating LinkedIn connection');
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('Error connecting to LinkedIn:', error);
      setMessage('❌ Network error. Please try again.');
      setTimeout(() => setMessage(''), 5000);
    }
  };

  const disconnectLinkedIn = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/integrations/linkedin`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        setLinkedInConnected(false);
        setLinkedInProfile(null);
        setMessage('✅ LinkedIn integration disconnected');
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('Error disconnecting LinkedIn:', error);
    }
  };

  const importLinkedInLearning = async () => {
    setImportingCerts(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/integrations/linkedin-learning/import-certificates`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setMessage(`✅ ${data.message}`);
        setTimeout(() => setMessage(''), 5000);
        
        // Refresh certificates
        checkLinkedInConnection();
      } else {
        const error = await response.json();
        setMessage(`❌ Error: ${error.detail}`);
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('Error importing LinkedIn Learning certificates:', error);
      setMessage('❌ Network error while importing certificates');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setImportingCerts(false);
    }
  };

  const syncLinkedInExperience = async () => {
    setSyncing(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/integrations/linkedin/sync-experience`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setMessage(`✅ ${data.message}`);
        setTimeout(() => setMessage(''), 5000);
      } else {
        const error = await response.json();
        setMessage(`❌ Error: ${error.detail}`);
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('Error syncing LinkedIn experience:', error);
      setMessage('❌ Network error while syncing');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setSyncing(false);
    }
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
      <h1 className="text-3xl font-bold text-gray-900 mb-8">🔗 Account Integrations</h1>

      {/* Status Message */}
      {message && (
        <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
          <p className="text-blue-800">{message}</p>
        </div>
      )}

      {/* Integration Cards */}
      <div className="space-y-6">
        
        {/* LinkedIn Integration */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-start justify-between">
            <div className="flex items-start">
              <div className="w-16 h-16 bg-blue-600 rounded-lg flex items-center justify-center mr-4">
                <svg className="w-8 h-8 text-white" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                </svg>
              </div>
              <div>
                <h3 className="text-xl font-semibold text-gray-900">LinkedIn Profile</h3>
                <p className="text-gray-600 mt-1">
                  Sync your professional experience and credentials from LinkedIn
                </p>
                
                {linkedInConnected && linkedInProfile && (
                  <div className="mt-3 p-3 bg-green-50 rounded-md">
                    <div className="flex items-center text-green-800">
                      <span className="mr-2">✅</span>
                      <span className="font-medium">
                        Connected: {linkedInProfile.first_name} {linkedInProfile.last_name}
                      </span>
                    </div>
                    <p className="text-green-600 text-sm mt-1">
                      Connected on {new Date(linkedInProfile.connected_at).toLocaleDateString()}
                    </p>
                  </div>
                )}
              </div>
            </div>
            
            <div className="flex flex-col space-y-2">
              {!linkedInConnected ? (
                <button
                  onClick={connectLinkedIn}
                  className="bg-blue-600 text-white px-6 py-2 rounded-md font-medium hover:bg-blue-700 transition-colors"
                >
                  Connect LinkedIn
                </button>
              ) : (
                <>
                  <button
                    onClick={syncLinkedInExperience}
                    disabled={syncing}
                    className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
                  >
                    {syncing ? 'Syncing...' : 'Sync Experience'}
                  </button>
                  <button
                    onClick={disconnectLinkedIn}
                    className="bg-gray-200 text-gray-700 px-6 py-2 rounded-md font-medium hover:bg-gray-300 transition-colors"
                  >
                    Disconnect
                  </button>
                </>
              )}
            </div>
          </div>

          {linkedInConnected && (
            <div className="mt-4 pt-4 border-t border-gray-200">
              <h4 className="font-medium text-gray-900 mb-3">LinkedIn Learning Certificates</h4>
              
              {linkedInCertificates.length > 0 ? (
                <div className="space-y-3">
                  {linkedInCertificates.map((cert, index) => (
                    <div key={index} className="bg-green-50 border border-green-200 rounded-md p-3">
                      <div className="flex items-center justify-between">
                        <div>
                          <h5 className="font-medium text-green-900">{cert.course_name}</h5>
                          <p className="text-sm text-green-700">
                            Completed: {new Date(cert.completion_date).toLocaleDateString()}
                          </p>
                          <div className="flex flex-wrap gap-1 mt-2">
                            {cert.skills.map((skill, skillIndex) => (
                              <span key={skillIndex} className="text-xs bg-green-200 text-green-800 px-2 py-1 rounded">
                                {skill}
                              </span>
                            ))}
                          </div>
                        </div>
                        <div className="text-green-600">
                          {cert.verified ? '✅' : '⏳'}
                        </div>
                      </div>
                    </div>
                  ))}
                  <button
                    onClick={importLinkedInLearning}
                    disabled={importingCerts}
                    className="w-full bg-blue-100 text-blue-800 px-4 py-2 rounded-md hover:bg-blue-200 transition-colors disabled:opacity-50"
                  >
                    {importingCerts ? 'Refreshing...' : '🔄 Refresh Certificates'}
                  </button>
                </div>
              ) : (
                <div className="text-center py-6">
                  <div className="text-4xl mb-2">📚</div>
                  <p className="text-gray-600 mb-4">No LinkedIn Learning certificates found</p>
                  <button
                    onClick={importLinkedInLearning}
                    disabled={importingCerts}
                    className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50"
                  >
                    {importingCerts ? 'Importing...' : '📚 Import Learning Certificates'}
                  </button>
                </div>
              )}
              
              <div className="mt-4 pt-4 border-t border-gray-200">
                <h4 className="font-medium text-gray-900 mb-2">Available Actions:</h4>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  <div className="flex items-center p-3 bg-blue-50 rounded-md">
                    <span className="mr-2">📋</span>
                    <span className="text-sm">Experience sync</span>
                  </div>
                  <div className="flex items-center p-3 bg-blue-50 rounded-md">
                    <span className="mr-2">🎓</span>
                    <span className="text-sm">Learning certificates</span>
                  </div>
                  <div className="flex items-center p-3 bg-gray-50 rounded-md">
                    <span className="mr-2">🏆</span>
                    <span className="text-sm">Skills validation (Coming soon)</span>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* GitHub Integration (Coming Soon) */}
        <div className="bg-white rounded-lg shadow-md p-6 opacity-75">
          <div className="flex items-start justify-between">
            <div className="flex items-start">
              <div className="w-16 h-16 bg-gray-800 rounded-lg flex items-center justify-center mr-4">
                <svg className="w-8 h-8 text-white" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                </svg>
              </div>
              <div>
                <h3 className="text-xl font-semibold text-gray-900">GitHub Profile</h3>
                <p className="text-gray-600 mt-1">
                  For agricultural technology professionals and developers
                </p>
                <div className="mt-2">
                  <span className="inline-block bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded-full">
                    Coming Soon
                  </span>
                </div>
              </div>
            </div>
            <button
              disabled
              className="bg-gray-300 text-gray-500 px-6 py-2 rounded-md font-medium cursor-not-allowed"
            >
              Coming Soon
            </button>
          </div>
        </div>

        {/* Google Integration (Coming Soon) */}
        <div className="bg-white rounded-lg shadow-md p-6 opacity-75">
          <div className="flex items-start justify-between">
            <div className="flex items-start">
              <div className="w-16 h-16 bg-blue-500 rounded-lg flex items-center justify-center mr-4">
                <svg className="w-8 h-8 text-white" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
              </div>
              <div>
                <h3 className="text-xl font-semibold text-gray-900">Google Workspace</h3>
                <p className="text-gray-600 mt-1">
                  Sync Google Drive documents and certifications
                </p>
                <div className="mt-2">
                  <span className="inline-block bg-yellow-100 text-yellow-800 text-xs px-2 py-1 rounded-full">
                    Coming Soon
                  </span>
                </div>
              </div>
            </div>
            <button
              disabled
              className="bg-gray-300 text-gray-500 px-6 py-2 rounded-md font-medium cursor-not-allowed"
            >
              Coming Soon
            </button>
          </div>
        </div>

      </div>

      {/* Integration Benefits */}
      <div className="mt-12 bg-green-50 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-green-900 mb-4">
          🌟 Benefits of Connecting Your Accounts
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="flex items-start">
            <span className="text-green-600 mr-3 mt-1">✓</span>
            <div>
              <h4 className="font-medium text-green-900">Automated Profile Updates</h4>
              <p className="text-green-700 text-sm">
                Keep your agricultural professional profile current with automatic syncing
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <span className="text-green-600 mr-3 mt-1">✓</span>
            <div>
              <h4 className="font-medium text-green-900">Enhanced Credibility</h4>
              <p className="text-green-700 text-sm">
                Verified professional history builds trust in the agricultural community
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <span className="text-green-600 mr-3 mt-1">✓</span>
            <div>
              <h4 className="font-medium text-green-900">Better Matching</h4>
              <p className="text-green-700 text-sm">
                More accurate professional matching with comprehensive data
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <span className="text-green-600 mr-3 mt-1">✓</span>
            <div>
              <h4 className="font-medium text-green-900">Time Savings</h4>
              <p className="text-green-700 text-sm">
                Reduce manual data entry with automatic imports
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Privacy Policy Link for Compliance */}
      <div className="mt-6 text-center border-t pt-4">
        <p className="text-sm text-gray-600">
          By connecting external accounts, you agree to our{' '}
          <a 
            href="/privacy" 
            target="_blank" 
            rel="noopener noreferrer"
            className="text-green-600 hover:text-green-700 underline"
          >
            Privacy Policy
          </a>
        </p>
      </div>
    </div>
  );
};

export default Integrations;