import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const EnhancedValidations = ({ user }) => {
  const [isCreating, setIsCreating] = useState(false);
  const [validations, setValidations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searchMode, setSearchMode] = useState('internal'); // 'internal' or 'external'
  const [searchLoading, setSearchLoading] = useState(false);

  const [formData, setFormData] = useState({
    skill_id: '',
    description: '',
    project_name: '',
    collaboration_period: '',
    specific_achievements: '',
    working_relationship: '',
    // Tagging system
    validated_user_id: null,
    external_profile: null,
    selected_person: null
  });

  const workingRelationships = [
    { value: 'direct_supervisor', label: 'Fui su supervisor directo' },
    { value: 'colleague', label: 'Trabajamos como colegas' },
    { value: 'client', label: 'Fui su cliente' },
    { value: 'team_member', label: 'Trabajamos en el mismo equipo' },
    { value: 'contractor', label: 'Trabajó como contratista para mí' }
  ];

  useEffect(() => {
    fetchValidations();
  }, []);

  const fetchValidations = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations/enhanced/received`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await response.json();
      setValidations(data);
    } catch (error) {
      console.error('Error fetching validations:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchPeople = async (query) => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }

    setSearchLoading(true);
    try {
      const token = localStorage.getItem('token');
      
      if (searchMode === 'internal') {
        // Search internal SHARKNO users
        const response = await fetch(`${API}/search/collaborators?q=${encodeURIComponent(query)}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        const results = data.map(user => ({
          type: 'internal',
          user_id: user.user_id,
          name: user.name,
          title: user.title,
          role: user.role,
          platform: 'sharkno'
        }));
        setSearchResults(results);
        
      } else {
        // Search external profiles (LinkedIn)
        const response = await fetch(`${API}/search/external-profiles?q=${encodeURIComponent(query)}&platform=linkedin`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        const results = data.map(profile => ({
          type: 'external',
          ...profile
        }));
        setSearchResults(results);
      }
    } catch (error) {
      console.error('Error searching people:', error);
    } finally {
      setSearchLoading(false);
    }
  };

  const selectPerson = (person) => {
    if (person.type === 'internal') {
      setFormData({
        ...formData,
        validated_user_id: person.user_id,
        external_profile: null,
        selected_person: person
      });
    } else {
      setFormData({
        ...formData,
        validated_user_id: null,
        external_profile: {
          platform: person.platform,
          platform_id: person.platform_id,
          profile_url: person.profile_url,
          name: person.name,
          title: person.title,
          company: person.company,
          email: person.email
        },
        selected_person: person
      });
    }
    setSearchQuery('');
    setSearchResults([]);
  };

  const clearSelection = () => {
    setFormData({
      ...formData,
      validated_user_id: null,
      external_profile: null,
      selected_person: null
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations/enhanced`, {
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
        resetForm();
        alert('✅ Validación creada exitosamente!');
      } else {
        const error = await response.json();
        alert(`❌ Error: ${error.detail}`);
      }
    } catch (error) {
      console.error('Error creating validation:', error);
      alert('❌ Error al crear la validación');
    }
  };

  const resetForm = () => {
    setFormData({
      skill_id: '',
      description: '',
      project_name: '',
      collaboration_period: '',
      specific_achievements: '',
      working_relationship: '',
      validated_user_id: null,
      external_profile: null,
      selected_person: null
    });
    setSearchQuery('');
    setSearchResults([]);
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
        <h1 className="text-3xl font-bold text-gray-900">✅ Validaciones Avanzadas</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
        >
          + Crear Validación
        </button>
      </div>

      {/* Explanation */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold text-blue-900 mb-2">🎯 Sistema de Etiquetado SHARKNO</h2>
        <div className="text-blue-700 space-y-2">
          <p>• <strong>Usuarios SHARKNO:</strong> Busca y etiqueta a profesionales que ya están en la plataforma</p>
          <p>• <strong>LinkedIn Integration:</strong> Etiqueta perfiles de LinkedIn e invítalos a unirse a SHARKNO</p>
          <p>• <strong>Notificaciones Automáticas:</strong> Las personas etiquetadas reciben invitaciones automáticamente</p>
          <p>• <strong>Validación Cruzada:</strong> Cuando se unan a SHARKNO, pueden aprobar tus validaciones</p>
        </div>
      </div>

      {/* Create Validation Modal */}
      {isCreating && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
          <div className="bg-white rounded-lg p-6 w-full max-w-3xl m-4 max-h-screen overflow-y-auto">
            <h2 className="text-2xl font-bold mb-6">✅ Crear Validación Avanzada</h2>
            
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Skill/Competency */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Habilidad/Competencia a Validar *
                </label>
                <input
                  type="text"
                  value={formData.skill_id}
                  onChange={(e) => setFormData({...formData, skill_id: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="ej. Manejo de Sistemas de Riego, Liderazgo de Equipos"
                  required
                />
              </div>

              {/* Person Selection */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Buscar Persona a Validar *
                </label>
                
                {/* Search Mode Toggle */}
                <div className="flex space-x-4 mb-3">
                  <button
                    type="button"
                    onClick={() => setSearchMode('internal')}
                    className={`px-4 py-2 rounded-md text-sm font-medium ${
                      searchMode === 'internal' 
                        ? 'bg-green-600 text-white' 
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    }`}
                  >
                    👥 Usuarios SHARKNO
                  </button>
                  <button
                    type="button"
                    onClick={() => setSearchMode('external')}
                    className={`px-4 py-2 rounded-md text-sm font-medium ${
                      searchMode === 'external' 
                        ? 'bg-blue-600 text-white' 
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    }`}
                  >
                    🔗 LinkedIn
                  </button>
                </div>

                {/* Selected Person Display */}
                {formData.selected_person && (
                  <div className="mb-3 p-3 bg-green-50 border border-green-200 rounded-md">
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="font-medium text-green-900">
                          {formData.selected_person.name}
                          {formData.selected_person.type === 'external' && (
                            <span className="ml-2 text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                              LinkedIn
                            </span>
                          )}
                        </div>
                        <div className="text-sm text-green-700">
                          {formData.selected_person.title} 
                          {formData.selected_person.company && ` - ${formData.selected_person.company}`}
                        </div>
                        {formData.selected_person.type === 'external' && (
                          <div className="text-xs text-blue-600 mt-1">
                            💌 Se enviará invitación automática a LinkedIn
                          </div>
                        )}
                      </div>
                      <button
                        type="button"
                        onClick={clearSelection}
                        className="text-red-600 hover:text-red-800"
                      >
                        ✕
                      </button>
                    </div>
                  </div>
                )}

                {/* Search Input */}
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                    searchPeople(e.target.value);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder={searchMode === 'internal' ? "Buscar usuarios SHARKNO..." : "Buscar perfiles LinkedIn..."}
                />

                {/* Search Results */}
                {searchLoading && (
                  <div className="mt-2 text-sm text-gray-500">🔍 Buscando...</div>
                )}
                
                {searchResults.length > 0 && (
                  <div className="mt-2 max-h-48 overflow-y-auto border border-gray-300 rounded-md">
                    {searchResults.map((person, index) => (
                      <div
                        key={index}
                        onClick={() => selectPerson(person)}
                        className="p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-200 last:border-b-0"
                      >
                        <div className="flex items-center justify-between">
                          <div>
                            <div className="font-medium flex items-center">
                              {person.name}
                              {person.type === 'external' && (
                                <span className="ml-2 text-xs bg-blue-100 text-blue-800 px-1 py-0.5 rounded">
                                  LinkedIn
                                </span>
                              )}
                            </div>
                            <div className="text-sm text-gray-600">
                              {person.title} {person.company && `- ${person.company}`}
                            </div>
                          </div>
                          <div className="text-2xl">
                            {person.type === 'internal' ? '👤' : '🔗'}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Project Context Fields */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre del Proyecto (Opcional)
                </label>
                <input
                  type="text"
                  value={formData.project_name}
                  onChange={(e) => setFormData({...formData, project_name: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="ej. Sistema de Riego Finca Los Naranjos"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tipo de Colaboración
                </label>
                <select
                  value={formData.working_relationship}
                  onChange={(e) => setFormData({...formData, working_relationship: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <option value="">Seleccionar tipo de colaboración</option>
                  {workingRelationships.map(rel => (
                    <option key={rel.value} value={rel.value}>{rel.label}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Período de Colaboración
                </label>
                <input
                  type="text"
                  value={formData.collaboration_period}
                  onChange={(e) => setFormData({...formData, collaboration_period: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="ej. Enero - Marzo 2024, 6 meses"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción de la Experiencia Compartida *
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Describe específicamente el proyecto o situación donde trabajaron juntos y cómo observaste las habilidades de esta persona..."
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Logros Específicos
                </label>
                <textarea
                  value={formData.specific_achievements}
                  onChange={(e) => setFormData({...formData, specific_achievements: e.target.value})}
                  rows="2"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="¿Qué logros específicos tuvo esta persona en el proyecto?"
                />
              </div>

              <div className="flex justify-end space-x-4">
                <button
                  type="button"
                  onClick={() => {
                    setIsCreating(false);
                    resetForm();
                  }}
                  className="px-6 py-2 text-gray-600 hover:text-gray-800"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={!formData.selected_person}
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
                >
                  {formData.external_profile ? '📧 Crear y Invitar' : 'Crear Validación'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Validations List */}
      <div className="space-y-4">
        {validations.length > 0 ? (
          validations.map((validation) => (
            <div key={validation.id} className="bg-white rounded-lg shadow-md p-6 border-l-4 border-green-500">
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    {validation.skill_id}
                  </h3>
                  <p className="text-gray-700 mb-3">{validation.description}</p>
                  
                  {validation.project_name && (
                    <div className="mb-2">
                      <span className="text-sm font-medium text-gray-600">Proyecto: </span>
                      <span className="text-sm text-gray-800">{validation.project_name}</span>
                    </div>
                  )}
                  
                  {validation.working_relationship && (
                    <div className="mb-2">
                      <span className="text-sm font-medium text-gray-600">Colaboración: </span>
                      <span className="text-sm text-gray-800">
                        {workingRelationships.find(r => r.value === validation.working_relationship)?.label || validation.working_relationship}
                      </span>
                    </div>
                  )}

                  {validation.external_profile && (
                    <div className="mt-3 p-2 bg-blue-50 rounded-md">
                      <span className="text-xs text-blue-600">
                        🔗 Validación desde LinkedIn - Invitación enviada
                      </span>
                    </div>
                  )}
                </div>
                
                <div className="text-sm text-gray-500">
                  {new Date(validation.created_at).toLocaleDateString()}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">✅</div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No hay validaciones aún</h3>
            <p className="text-gray-600 mb-4">
              Comienza validando las habilidades de profesionales con los que has trabajado
            </p>
            <button
              onClick={() => setIsCreating(true)}
              className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
            >
              + Crear Primera Validación
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default EnhancedValidations;