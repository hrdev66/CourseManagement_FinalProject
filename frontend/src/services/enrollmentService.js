import api from './api';

export const enrollmentService = {
  getAll: () => api.get('/enrollments'),
  getById: (id) => api.get(`/enrollments/${id}`),
  create: (data) => api.post('/enrollments', data),
  update: (id, data) => api.put(`/enrollments/${id}`, data),
  delete: (id) => api.delete(`/enrollments/${id}`),
  getByStudent: (studentId) => api.get(`/enrollments/student/${studentId}`),
  getByCourse: (courseId) => api.get(`/enrollments/course/${courseId}`),
};

