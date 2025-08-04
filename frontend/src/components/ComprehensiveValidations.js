import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const ComprehensiveValidations = ({ user }) => {
  const [isCreating, setIsCreating] = useState(false);
  const [validations, setValidations] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Entity search and tagging
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedEntityType, setSelectedEntityType] = useState('all');
  const [taggedEntities, setTaggedEntities] = useState([]);

  const [formData, setFormData] = useState({
    skill_id: '',
    description: '',
    project_name: '',
    collaboration_period: '',
    specific_achievements: '',
    working_relationship: '',
    quantified_results: '',
    impact_metrics: []
  });

  const entityTypes = [
    { value: 'all', label: '🔍 Buscar Todo', icon: '🔍' },
    { value: 'person', label: '👥 Personas', icon: '👤' },
    { value: 'company', label: '🏢 Empresas', icon: '🏢' },
    { value: 'product', label: '🚜 Productos/Equipos', icon: '🚜' },
    { value: 'location', label: '📍 Ubicaciones', icon: '📍' },
    { value: 'crop', label: '🌾 Cultivos', icon: '🌾' }
  ];

  const workingRelationships = [
    { value: 'direct_supervisor', label: 'Fui su supervisor directo' },
    { value: 'colleague', label: 'Trabajamos como colegas' },
    { value: 'client', label: 'Fui su cliente' },
    { value: 'team_member', label: 'Trabajamos en el mismo equipo' },
    { value: 'contractor', label: 'Trabajó como contratista para mí' }
  ];

  const impactMetrics = [
    { value: 'water_savings', label: '💧 Ahorro de Agua' },
    { value: 'cost_reduction', label: '💰 Reducción de Costos' },
    { value: 'yield_increase', label: '📈 Aumento de Rendimiento' },
    { value: 'efficiency_improvement', label: '⚡ Mejora de Eficiencia' },
    { value: 'quality_enhancement', label: '⭐ Mejora de Calidad' },
    { value: 'time_savings', label: '⏱️ Ahorro de Tiempo' },
    { value: 'environmental_benefit', label: '🌱 Beneficio Ambiental' }
  ];

  useEffect(() => {
    fetchValidations();
  }, []);

  const fetchValidations = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/validations/comprehensive/received`, {
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

  const searchEntities = async (query) => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }

    setSearchLoading(true);
    try {
      const token = localStorage.getItem('token');
      const entityTypeParam = selectedEntityType === 'all' ? '' : `&entity_type=${selectedEntityType}`;
      const response = await fetch(`${API}/search/entities?q=${encodeURIComponent(query)}${entityTypeParam}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await response.json();
      setSearchResults(data);
    } catch (error) {
      console.error('Error searching entities:', error);
    } finally {
      setSearchLoading(false);
    }
  };

  const addTaggedEntity = (entity) => {
    // Check if entity is already tagged
    const isAlreadyTagged = taggedEntities.some(tagged => 
      tagged.entity_type === entity.entity_type && 
      tagged.name === entity.name
    );

    if (!isAlreadyTagged) {
      setTaggedEntities([...taggedEntities, entity]);
    }
    
    setSearchQuery('');
    setSearchResults([]);
  };

  const removeTaggedEntity = (index) => {
    setTaggedEntities(taggedEntities.filter((_, i) => i !== index));
  };

  const getEntityIcon = (entityType) => {
    const icons = {
      person: '👤',
      company: '🏢',
      product: '🚜',
      location: '📍',
      crop: '🌾'
    };
    return icons[entityType] || '🏷️';
  };

  const getEntityDescription = (entity) => {
    switch (entity.entity_type) {
      case 'person':
        return `${entity.title || 'Professional'}${entity.company ? ` - ${entity.company}` : ''}${entity.platform ? ` (${entity.platform})` : ''}`;
      case 'company':
        return entity.industry || 'Company';
      case 'product':
        return `${entity.category || 'Product'}${entity.brand ? ` - ${entity.brand}` : ''}${entity.model ? ` ${entity.model}` : ''}`;
      case 'location':
        return entity.address || 'Location';
      case 'crop':
        return `${entity.variety || 'Variety'}${entity.season ? ` - ${entity.season}` : ''}`;
      default:
        return 'Entity';
    }
  };

  const handleMetricToggle = (metric) => {
    if (formData.impact_metrics.includes(metric)) {
      setFormData({
        ...formData,
        impact_metrics: formData.impact_metrics.filter(m => m !== metric)
      });
    } else {
      setFormData({
        ...formData,
        impact_metrics: [...formData.impact_metrics, metric]
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (taggedEntities.length === 0) {
      alert('❌ Debes etiquetar al menos una entidad (persona, empresa, producto, etc.)');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const validationData = {
        ...formData,
        tagged_entities: taggedEntities
      };

      const response = await fetch(`${API}/validations/comprehensive`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(validationData)
      });

      if (response.ok) {
        await fetchValidations();
        setIsCreating(false);
        resetForm();
        alert('✅ Validación integral creada exitosamente!');
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
      quantified_results: '',
      impact_metrics: []
    });
    setTaggedEntities([]);
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
        <h1 className="text-3xl font-bold text-gray-900">🏷️ Validaciones Integrales</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
        >
          + Crear Validación
        </button>
      </div>

      {/* Explanation */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold text-blue-900 mb-2">🎯 Sistema de Etiquetado Completo - Como LinkedIn</h2>
        <div className="text-blue-700 space-y-2">
          <p>• <strong>👤 Personas:</strong> Etiqueta usuarios SHARKNO o perfiles LinkedIn de colegas</p>
          <p>• <strong>🏢 Empresas:</strong> Menciona las empresas involucradas (John Deere, Syngenta, etc.)</p>
          <p>• <strong>🚜 Productos/Equipos:</strong> Especifica qué tecnología, maquinaria o insumos se usaron</p>
          <p>• <strong>📍 Ubicaciones:</strong> Menciona fincas, instalaciones o ubicaciones específicas</p>
          <p>• <strong>🌾 Cultivos:</strong> Especifica variedades de cultivos involucradas</p>
          <p>• <strong>📊 Impacto Cuantificado:</strong> Agrega métricas de resultados reales</p>
        </div>
      </div>

      {/* Create Validation Modal */}
      {isCreating && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl m-4 max-h-screen overflow-y-auto">
            <h2 className="text-2xl font-bold mb-6">🏷️ Crear Validación Integral</h2>
            
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
                  placeholder="ej. Implementación de Sistema de Riego Inteligente"
                  required
                />
              </div>

              {/* Entity Tagging Section */}
              <div className="border-2 border-green-200 rounded-lg p-4 bg-green-50">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  🏷️ Etiquetar Entidades Involucradas *
                </label>
                
                {/* Entity Type Filter */}
                <div className="flex flex-wrap gap-2 mb-3">
                  {entityTypes.map(type => (
                    <button
                      key={type.value}
                      type="button"
                      onClick={() => setSelectedEntityType(type.value)}
                      className={`px-3 py-1 rounded-full text-sm font-medium ${
                        selectedEntityType === type.value 
                          ? 'bg-green-600 text-white' 
                          : 'bg-white text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      <span className="mr-1">{type.icon}</span>
                      {type.label}
                    </button>
                  ))}
                </div>

                {/* Tagged Entities Display */}
                {taggedEntities.length > 0 && (
                  <div className="mb-4">
                    <h4 className="font-medium text-gray-900 mb-2">Entidades Etiquetadas:</h4>
                    <div className="flex flex-wrap gap-2">
                      {taggedEntities.map((entity, index) => (
                        <div key={index} className="bg-white border border-gray-300 rounded-md px-3 py-2 flex items-center">
                          <span className="mr-2">{getEntityIcon(entity.entity_type)}</span>
                          <div className="flex-1">
                            <div className="font-medium text-sm">{entity.name}</div>
                            <div className="text-xs text-gray-500">{getEntityDescription(entity)}</div>
                          </div>
                          <button
                            type="button"
                            onClick={() => removeTaggedEntity(index)}
                            className="ml-2 text-red-600 hover:text-red-800"
                          >
                            ✕
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Search Input */}
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                    searchEntities(e.target.value);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder={`Buscar ${selectedEntityType === 'all' ? 'personas, empresas, productos...' : entityTypes.find(t => t.value === selectedEntityType)?.label.toLowerCase() || 'entidades'}`}
                />

                {/* Search Results */}
                {searchLoading && (
                  <div className="mt-2 text-sm text-gray-500">🔍 Buscando...</div>
                )}
                
                {searchResults.length > 0 && (
                  <div className="mt-2 max-h-48 overflow-y-auto border border-gray-300 rounded-md bg-white">
                    {searchResults.map((entity, index) => (
                      <div
                        key={index}
                        onClick={() => addTaggedEntity(entity)}
                        className="p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-200 last:border-b-0"
                      >
                        <div className="flex items-center">
                          <span className="mr-3 text-xl">{getEntityIcon(entity.entity_type)}</span>
                          <div className="flex-1">
                            <div className="font-medium">{entity.name}</div>
                            <div className="text-sm text-gray-600">{getEntityDescription(entity)}</div>
                          </div>
                          <div className="text-xs bg-gray-100 px-2 py-1 rounded">
                            {entity.entity_type}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Project Context */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre del Proyecto
                  </label>
                  <input
                    type="text"
                    value={formData.project_name}
                    onChange={(e) => setFormData({...formData, project_name: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="ej. Modernización Sistema de Riego Finca Los Naranjos"
                  />
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
                    placeholder="ej. Enero - Marzo 2024"
                  />
                </div>
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

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción de la Experiencia *
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Describe específicamente el proyecto, el rol de las personas etiquetadas, cómo se usaron los productos/equipos, y qué se logró..."
                  required
                />
              </div>

              {/* Quantified Results */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Resultados Cuantificados
                </label>
                <textarea
                  value={formData.quantified_results}
                  onChange={(e) => setFormData({...formData, quantified_results: e.target.value})}
                  rows="2"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="ej. Redujo el consumo de agua en 40%, aumentó el rendimiento en 25%, ahorró €5,000 en costos operativos"
                />
              </div>

              {/* Impact Metrics */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Métricas de Impacto
                </label>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                  {impactMetrics.map(metric => (
                    <div key={metric.value} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={formData.impact_metrics.includes(metric.value)}
                        onChange={() => handleMetricToggle(metric.value)}
                        className="mr-2"
                      />
                      <label className="text-sm text-gray-700">{metric.label}</label>
                    </div>
                  ))}
                </div>
              </div>

              {/* Specific Achievements */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Logros Específicos
                </label>
                <textarea
                  value={formData.specific_achievements}
                  onChange={(e) => setFormData({...formData, specific_achievements: e.target.value})}
                  rows="2"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="¿Qué logros específicos se alcanzaron gracias a las entidades etiquetadas?"
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
                  disabled={taggedEntities.length === 0}
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
                >
                  🏷️ Crear Validación Integral
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
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    {validation.skill_id}
                  </h3>
                  
                  {/* Tagged Entities Display */}
                  {validation.tagged_entities && validation.tagged_entities.length > 0 && (
                    <div className="mb-3">
                      <h4 className="text-sm font-medium text-gray-600 mb-1">🏷️ Entidades Etiquetadas:</h4>
                      <div className="flex flex-wrap gap-1">
                        {validation.tagged_entities.map((entity, index) => (
                          <span 
                            key={index} 
                            className="inline-flex items-center px-2 py-1 bg-gray-100 text-gray-700 text-xs rounded-md"
                          >
                            <span className="mr-1">{getEntityIcon(entity.entity_type)}</span>
                            {entity.name}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                  
                  <p className="text-gray-700 mb-3">{validation.description}</p>
                  
                  {validation.quantified_results && (
                    <div className="mb-2 p-2 bg-green-50 rounded-md">
                      <span className="text-sm font-medium text-green-800">📊 Resultados: </span>
                      <span className="text-sm text-green-700">{validation.quantified_results}</span>
                    </div>
                  )}

                  {validation.impact_metrics && validation.impact_metrics.length > 0 && (
                    <div className="flex flex-wrap gap-1 mt-2">
                      {validation.impact_metrics.map((metric, index) => {
                        const metricInfo = impactMetrics.find(m => m.value === metric);
                        return metricInfo ? (
                          <span key={index} className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                            {metricInfo.label}
                          </span>
                        ) : null;
                      })}
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
            <div className="text-6xl mb-4">🏷️</div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No hay validaciones integrales aún</h3>
            <p className="text-gray-600 mb-4">
              Comienza creando validaciones que etiqueten personas, empresas, productos y más
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

export default ComprehensiveValidations;