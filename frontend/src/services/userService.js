import api from "./api";

const userService = {
  // Lấy danh sách tất cả users
  getAllUsers: async () => {
    const response = await api.get("/users");
    return response.data;
  },

  // Lấy thông tin một user
  getUserById: async (userId) => {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },

  // Cập nhật role của user
  updateUserRole: async (userId, role) => {
    const response = await api.put(`/users/${userId}/role`, { role });
    return response.data;
  },

  // Xóa user
  deleteUser: async (userId) => {
    const response = await api.delete(`/users/${userId}`);
    return response.data;
  },
};

export default userService;

