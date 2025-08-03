import React, { useState, useEffect } from 'react';

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const Projects = ({ user }) => {
  const [projects, setProjects] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [isValidating, setIsValidating] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchCollaborator, setSearchCollaborator] = useState('');
  const [collaboratorResults, setCollaboratorResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);

  const [projectData, setProjectData] = useState({
    project_name: '',
    project_type: '',
    description: '',
    location: '',
    start_date: '',
    end_date: '',
    still_active: false,
    skills_demonstrated: [],
    collaborators: [],
    project_results: ''
  });

  const [validationData, setValidationData] = useState({
    project_experience_id: '',
    validated_user_id: '',
    project_role: '',
    skills_validated: [],
    collaboration_description: '',
    performance_rating: 5,
    would_work_again: true,
    validation_evidence: ''
  });

  const projectTypes = [
    'irrigation',
    'crop_management', 
    'livestock',
    'technology_implementation',
    'soil_improvement',
    'pest_control',
    'harvest_optimization',
    'equipment_installation',
    'consulting_project',
    'training_program'
  ];

  const commonSkills = [
    'Project Management',
    'Technical Problem Solving',
    'Team Leadership',
    'Agricultural Technology',
    'Irrigation Systems',
    'Crop Planning',
    'Livestock Management',
    'Equipment Operation',
    'Data Analysis',
    'Client Communication',
    'Quality Control',
    'Budget Management',
    'Training & Education',
    'Innovation Implementation',
    'Risk Assessment'
  ];

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/projects`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setProjects(data);
    } catch (error) {
      console.error('Error fetching projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchCollaborators = async (query) => {
    if (!query.trim()) {
      setCollaboratorResults([]);
      return;
    }

    setSearchLoading(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/search/collaborators?q=${encodeURIComponent(query)}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setCollaboratorResults(data);
    } catch (error) {
      console.error('Error searching collaborators:', error);
    } finally {
      setSearchLoading(false);
    }
  };

  const addCollaborator = (collaborator) => {
    if (!projectData.collaborators.find(c => c.user_id === collaborator.user_id)) {
      setProjectData({
        ...projectData,
        collaborators: [...projectData.collaborators, collaborator]
      });
    }
    setSearchCollaborator('');
    setCollaboratorResults([]);
  };

  const removeCollaborator = (userId) => {
    setProjectData({
      ...projectData,
      collaborators: projectData.collaborators.filter(c => c.user_id !== userId)
    });
  };

  const handleSkillToggle = (skill) => {
    if (projectData.skills_demonstrated.includes(skill)) {
      setProjectData({
        ...projectData,
        skills_demonstrated: projectData.skills_demonstrated.filter(s => s !== skill)
      });
    } else {
      setProjectData({
        ...projectData,
        skills_demonstrated: [...projectData.skills_demonstrated, skill]
      });
    }
  };

  const handleValidationSkillToggle = (skill) => {
    if (validationData.skills_validated.includes(skill)) {
      setValidationData({
        ...validationData,
        skills_validated: validationData.skills_validated.filter(s => s !== skill)
      });
    } else {
      setValidationData({
        ...validationData,
        skills_validated: [...validationData.skills_validated, skill]
      });
    }
  };

  const handleCreateProject = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      
      const projectPayload = {
        ...projectData,
        start_date: new Date(projectData.start_date).toISOString(),
        end_date: projectData.end_date ? new Date(projectData.end_date).toISOString() : null,
        collaborators: projectData.collaborators.map(c => c.user_id)
      };

      const response = await fetch(`${API}/projects`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(projectPayload)
      });

      if (response.ok) {
        await fetchProjects();
        setIsCreating(false);
        resetProjectData();
      }
    } catch (error) {
      console.error('Error creating project:', error);
    }
  };

  const handleCreateValidation = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API}/projects/validate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(validationData)
      });

      if (response.ok) {
        setIsValidating(false);
        resetValidationData();
        // Show success message
        alert('¡Validación de proyecto creada exitosamente!');
      }
    } catch (error) {
      console.error('Error creating validation:', error);
    }
  };

  const resetProjectData = () => {
    setProjectData({
      project_name: '',
      project_type: '',
      description: '',
      location: '',
      start_date: '',
      end_date: '',
      still_active: false,
      skills_demonstrated: [],
      collaborators: [],
      project_results: ''
    });
  };

  const resetValidationData = () => {
    setValidationData({
      project_experience_id: '',
      validated_user_id: '',
      project_role: '',
      skills_validated: [],
      collaboration_description: '',
      performance_rating: 5,
      would_work_again: true,
      validation_evidence: ''
    });
  };

  const openValidationModal = (project) => {
    setSelectedProject(project);
    setValidationData({
      ...validationData,
      project_experience_id: project.id
    });
    setIsValidating(true);
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
        <h1 className="text-3xl font-bold text-gray-900">🚜 Experiencias en Proyectos Agrícolas</h1>
        <button
          onClick={() => setIsCreating(true)}
          className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
        >
          + Agregar Proyecto
        </button>
      </div>

      {/* Explanation Section */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
        <h2 className="text-lg font-semibold text-blue-900 mb-2">🎯 La Clave de SHARKNO: Validación por Experiencias Reales</h2>
        <div className="text-blue-700 space-y-2">
          <p>• <strong>Registra Proyectos:</strong> Documenta proyectos agrícolas donde has trabajado (sistemas de riego, manejo de cultivos, etc.)</p>
          <p>• <strong>Invita Colaboradores:</strong> Agrega a las personas con las que trabajaste en cada proyecto</p>
          <p>• <strong>Valida Experiencias:</strong> Los colaboradores pueden validar las habilidades específicas que observaron</p>
          <p>• <strong>Construye Credibilidad:</strong> Crea un historial verificable de experiencias reales, no solo títulos</p>
        </div>
      </div>

      {/* Create Project Modal */}
      {isCreating && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl m-4 max-h-screen overflow-y-auto">
            <h2 className="text-2xl font-bold mb-6">🚜 Crear Nueva Experiencia de Proyecto</h2>
            <form onSubmit={handleCreateProject} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre del Proyecto *
                  </label>
                  <input
                    type="text"
                    value={projectData.project_name}
                    onChange={(e) => setProjectData({...projectData, project_name: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="ej. Implementación Sistema de Riego por Goteo"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Tipo de Proyecto *
                  </label>
                  <select
                    value={projectData.project_type}
                    onChange={(e) => setProjectData({...projectData, project_type: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    required
                  >
                    <option value="">Selecciona tipo</option>
                    {projectTypes.map(type => (
                      <option key={type} value={type}>
                        {type.replace('_', ' ').toUpperCase()}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Ubicación *
                  </label>
                  <input
                    type="text"
                    value={projectData.location}
                    onChange={(e) => setProjectData({...projectData, location: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    placeholder="ej. Finca Los Naranjos, Región del Maule"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Fecha de Inicio *
                  </label>
                  <input
                    type="date"
                    value={projectData.start_date}
                    onChange={(e) => setProjectData({...projectData, start_date: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Fecha de Finalización
                  </label>
                  <input
                    type="date"
                    value={projectData.end_date}
                    onChange={(e) => setProjectData({...projectData, end_date: e.target.value})}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                    disabled={projectData.still_active}
                  />
                </div>

                <div className="flex items-center">
                  <input
                    type="checkbox"
                    checked={projectData.still_active}
                    onChange={(e) => setProjectData({...projectData, still_active: e.target.checked, end_date: e.target.checked ? '' : projectData.end_date})}
                    className="mr-2"
                  />
                  <label className="text-sm text-gray-700">Proyecto aún activo</label>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción del Proyecto *
                </label>
                <textarea
                  value={projectData.description}
                  onChange={(e) => setProjectData({...projectData, description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Describe el proyecto, objetivos, desafíos enfrentados y tu rol específico..."
                  required
                />
              </div>

              {/* Skills Demonstrated */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Habilidades Demostradas en este Proyecto
                </label>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
                  {commonSkills.map(skill => (
                    <div key={skill} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={projectData.skills_demonstrated.includes(skill)}
                        onChange={() => handleSkillToggle(skill)}
                        className="mr-2"
                      />
                      <label className="text-sm text-gray-700">{skill}</label>
                    </div>
                  ))}
                </div>
              </div>

              {/* Collaborator Search */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Buscar Colaboradores que Trabajaron en este Proyecto
                </label>
                <input
                  type="text"
                  value={searchCollaborator}
                  onChange={(e) => {
                    setSearchCollaborator(e.target.value);
                    searchCollaborators(e.target.value);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Buscar por nombre o email..."
                />
                
                {searchLoading && (
                  <div className="mt-2 text-sm text-gray-500">Buscando...</div>
                )}
                
                {collaboratorResults.length > 0 && (
                  <div className="mt-2 max-h-48 overflow-y-auto border border-gray-300 rounded-md">
                    {collaboratorResults.map((collaborator) => (
                      <div
                        key={collaborator.user_id}
                        onClick={() => addCollaborator(collaborator)}
                        className="p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-200 last:border-b-0"
                      >
                        <div className="font-medium">{collaborator.name}</div>
                        <div className="text-sm text-gray-600">{collaborator.title} - {collaborator.role}</div>
                      </div>
                    ))}
                  </div>
                )}

                {/* Selected Collaborators */}
                {projectData.collaborators.length > 0 && (
                  <div className="mt-4">
                    <h4 className="font-medium text-gray-900 mb-2">Colaboradores Seleccionados:</h4>
                    <div className="space-y-2">
                      {projectData.collaborators.map((collaborator) => (
                        <div key={collaborator.user_id} className="flex items-center justify-between bg-green-50 p-2 rounded-md">
                          <div>
                            <span className="font-medium">{collaborator.name}</span>
                            <span className="text-sm text-gray-600 ml-2">({collaborator.role})</span>
                          </div>
                          <button
                            type="button"
                            onClick={() => removeCollaborator(collaborator.user_id)}
                            className="text-red-600 hover:text-red-800"
                          >
                            ✕
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Resultados del Proyecto
                </label>
                <textarea
                  value={projectData.project_results}
                  onChange={(e) => setProjectData({...projectData, project_results: e.target.value})}
                  rows="3"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="¿Qué se logró? Impacto, mejoras, aprendizajes..."
                />
              </div>

              <div className="flex justify-end space-x-4">
                <button
                  type="button"
                  onClick={() => {
                    setIsCreating(false);
                    resetProjectData();
                  }}
                  className="px-6 py-2 text-gray-600 hover:text-gray-800"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
                >
                  Crear Proyecto
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Validation Modal */}
      {isValidating && selectedProject && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
          <div className="bg-white rounded-lg p-6 w-full max-w-3xl m-4 max-h-screen overflow-y-auto">
            <h2 className="text-2xl font-bold mb-4">✅ Validar Colaborador del Proyecto</h2>
            <div className="bg-blue-50 p-4 rounded-md mb-6">
              <h3 className="font-medium text-blue-900">Proyecto: {selectedProject.project_name}</h3>
              <p className="text-sm text-blue-700">Ubicación: {selectedProject.location}</p>
            </div>

            <form onSubmit={handleCreateValidation} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Seleccionar Colaborador a Validar *
                </label>
                <select
                  value={validationData.validated_user_id}
                  onChange={(e) => setValidationData({...validationData, validated_user_id: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                >
                  <option value="">Seleccionar colaborador...</option>
                  {selectedProject.collaborators?.filter(id => id !== user.id).map(collaboratorId => (
                    <option key={collaboratorId} value={collaboratorId}>
                      Colaborador {collaboratorId} {/* En producción, mostrarías el nombre real */}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Rol del Colaborador en el Proyecto *
                </label>
                <input
                  type="text"
                  value={validationData.project_role}
                  onChange={(e) => setValidationData({...validationData, project_role: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="ej. Técnico de Riego, Supervisor de Campo, Coordinador de Proyecto"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Habilidades Específicas que Observaste *
                </label>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
                  {commonSkills.map(skill => (
                    <div key={skill} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={validationData.skills_validated.includes(skill)}
                        onChange={() => handleValidationSkillToggle(skill)}
                        className="mr-2"
                      />
                      <label className="text-sm text-gray-700">{skill}</label>
                    </div>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Calificación del Desempeño *
                </label>
                <select
                  value={validationData.performance_rating}
                  onChange={(e) => setValidationData({...validationData, performance_rating: parseInt(e.target.value)})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  required
                >
                  <option value={5}>⭐⭐⭐⭐⭐ Excelente</option>
                  <option value={4}>⭐⭐⭐⭐ Muy Bueno</option>
                  <option value={3}>⭐⭐⭐ Bueno</option>
                  <option value={2}>⭐⭐ Regular</option>
                  <option value={1}>⭐ Necesita Mejorar</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción de la Colaboración *
                </label>
                <textarea
                  value={validationData.collaboration_description}
                  onChange={(e) => setValidationData({...validationData, collaboration_description: e.target.value})}
                  rows="4"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="Describe específicamente cómo trabajaron juntos, qué tareas realizó, cómo fue su desempeño..."
                  required
                />
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  checked={validationData.would_work_again}
                  onChange={(e) => setValidationData({...validationData, would_work_again: e.target.checked})}
                  className="mr-2"
                />
                <label className="text-sm text-gray-700">¿Trabajarías nuevamente con esta persona?</label>
              </div>

              <div className="flex justify-end space-x-4">
                <button
                  type="button"
                  onClick={() => {
                    setIsValidating(false);
                    resetValidationData();
                  }}
                  className="px-6 py-2 text-gray-600 hover:text-gray-800"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
                >
                  Crear Validación
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Projects List */}
      <div className="space-y-6">
        {projects.length > 0 ? (
          projects.map((project) => (
            <div key={project.id} className="bg-white rounded-lg shadow-md p-6 border-l-4 border-green-500">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center space-x-3 mb-2">
                    <h3 className="text-xl font-bold text-gray-900">{project.project_name}</h3>
                    <span className="px-3 py-1 bg-green-100 text-green-800 text-sm font-medium rounded-full">
                      {project.project_type.replace('_', ' ').toUpperCase()}
                    </span>
                  </div>
                  
                  <div className="flex items-center space-x-4 text-sm text-gray-600 mb-3">
                    <span>📍 {project.location}</span>
                    <span>📅 {formatDate(project.start_date)} - {project.end_date ? formatDate(project.end_date) : 'Activo'}</span>
                    <span>👥 {project.collaborators?.length || 0} colaboradores</span>
                  </div>
                  
                  <p className="text-gray-700 mb-4">{project.description}</p>
                  
                  {project.skills_demonstrated?.length > 0 && (
                    <div className="mb-4">
                      <h4 className="font-medium text-gray-900 mb-2">🎯 Habilidades Demostradas:</h4>
                      <div className="flex flex-wrap gap-2">
                        {project.skills_demonstrated.map((skill) => (
                          <span key={skill} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded">
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                  
                  {project.project_results && (
                    <div className="mb-4">
                      <h4 className="font-medium text-gray-900 mb-1">📈 Resultados:</h4>
                      <p className="text-gray-600 text-sm">{project.project_results}</p>
                    </div>
                  )}
                </div>
                
                <button
                  onClick={() => openValidationModal(project)}
                  className="bg-green-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-green-700 transition-colors"
                >
                  ✅ Validar Colaborador
                </button>
              </div>
            </div>
          ))
        ) : (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">🚜</div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Aún no hay proyectos registrados</h3>
            <p className="text-gray-600 mb-4">
              Comienza documentando proyectos agrícolas donde has trabajado para construir tu credibilidad profesional
            </p>
            <button
              onClick={() => setIsCreating(true)}
              className="bg-green-600 text-white px-6 py-2 rounded-md font-medium hover:bg-green-700 transition-colors"
            >
              + Registrar Mi Primer Proyecto
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Projects;