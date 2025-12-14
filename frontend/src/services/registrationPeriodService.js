import api from './api';

export const registrationPeriodService = {
  // Lấy tất cả đợt đăng ký
  getAll: () => api.get('/registration-periods'),
  
  // Lấy đợt đăng ký theo ID
  getById: (id) => api.get(`/registration-periods/${id}`),
  
  // Lấy các đợt đăng ký đang active
  getActive: () => api.get('/registration-periods/active'),
  
  // Tạo đợt đăng ký mới
  create: (data) => api.post('/registration-periods', data),
  
  // Cập nhật đợt đăng ký
  update: (id, data) => api.put(`/registration-periods/${id}`, data),
  
  // Xóa đợt đăng ký
  delete: (id) => api.delete(`/registration-periods/${id}`),
  
  // Lấy các khóa học trong đợt đăng ký
  getCourses: (periodId) => api.get(`/registration-periods/${periodId}/courses`),
  
  // Cập nhật các khóa học trong đợt đăng ký
  updateCourses: (periodId, courseIds) => api.put(`/registration-periods/${periodId}/courses`, courseIds),
  
  // Lấy chi tiết đợt đăng ký (bao gồm courses)
  getDetails: (periodId) => api.get(`/registration-periods/${periodId}/details`),
};

export default registrationPeriodService;

