import api from './api';

export const submissionService = {
  getAll: () => api.get('/submissions'),
  getById: (id) => api.get(`/submissions/${id}`),
  create: (data) => api.post('/submissions', data),
  update: (id, data) => api.put(`/submissions/${id}`, data),
  delete: (id) => api.delete(`/submissions/${id}`),
  getByAssignment: (assignmentId) => api.get(`/submissions/assignment/${assignmentId}`),
  getByStudent: (studentId) => api.get(`/submissions/student/${studentId}`),
};

