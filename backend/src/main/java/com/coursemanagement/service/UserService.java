package com.coursemanagement.service;

import com.coursemanagement.entity.User;
import com.coursemanagement.entity.User.UserRole;
import com.coursemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy danh sách tất cả users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Lấy user theo ID
     */
    public User getUserById(Integer userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng với ID: " + userId));
    }

    /**
     * Cập nhật role cho user
     */
    @Transactional
    public User updateUserRole(Integer userId, String newRole) throws Exception {
        User user = getUserById(userId);
        
        // Validate role
        UserRole role;
        try {
            role = UserRole.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Role không hợp lệ. Các role được phép: ADMIN, INSTRUCTOR, STUDENT");
        }
        
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Xóa user
     */
    @Transactional
    public void deleteUser(Integer userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new Exception("Không tìm thấy người dùng với ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Kiểm tra xem có phải admin duy nhất không
     */
    public boolean isLastAdmin(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return false;
        }
        
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .count();
        
        return adminCount <= 1;
    }
}

