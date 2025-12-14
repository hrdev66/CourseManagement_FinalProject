package com.coursemanagement.controller;

import com.coursemanagement.dto.UpdateRoleRequest;
import com.coursemanagement.entity.User;
import com.coursemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Lấy danh sách tất cả users (chỉ admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            
            // Map to DTO to hide password
            List<Map<String, Object>> userDtos = users.stream().map(user -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("userId", user.getUserId());
                dto.put("username", user.getUsername());
                dto.put("email", user.getEmail());
                dto.put("role", user.getRole() != null ? user.getRole().toString() : "STUDENT");
                dto.put("referenceId", user.getReferenceId());
                dto.put("createdAt", user.getCreatedAt());
                dto.put("lastLogin", user.getLastLogin());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            e.printStackTrace(); // Log chi tiết lỗi
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định: " + e.getClass().getSimpleName();
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    /**
     * Lấy thông tin một user theo ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
        try {
            User user = userService.getUserById(userId);
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("userId", user.getUserId());
            dto.put("username", user.getUsername());
            dto.put("email", user.getEmail());
            dto.put("role", user.getRole() != null ? user.getRole().toString() : "STUDENT");
            dto.put("referenceId", user.getReferenceId());
            dto.put("createdAt", user.getCreatedAt());
            dto.put("lastLogin", user.getLastLogin());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định: " + e.getClass().getSimpleName();
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    /**
     * Cập nhật role của user (chỉ admin)
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Integer userId,
            @RequestBody UpdateRoleRequest request) {
        try {
            // Check if this is the last admin
            if (userService.isLastAdmin(userId) && !"ADMIN".equalsIgnoreCase(request.getRole())) {
                return ResponseEntity.badRequest()
                        .body("Không thể thay đổi role của admin cuối cùng. Hệ thống cần ít nhất một admin.");
            }
            
            User updatedUser = userService.updateUserRole(userId, request.getRole());
            
            Map<String, Object> dto = new HashMap<>();
            dto.put("userId", updatedUser.getUserId());
            dto.put("username", updatedUser.getUsername());
            dto.put("email", updatedUser.getEmail());
            dto.put("role", updatedUser.getRole() != null ? updatedUser.getRole().toString() : "STUDENT");
            dto.put("referenceId", updatedUser.getReferenceId());
            dto.put("createdAt", updatedUser.getCreatedAt());
            dto.put("lastLogin", updatedUser.getLastLogin());
            dto.put("message", "Cập nhật role thành công");
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định: " + e.getClass().getSimpleName();
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    /**
     * Xóa user (chỉ admin)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            // Check if this is the last admin
            if (userService.isLastAdmin(userId)) {
                return ResponseEntity.badRequest()
                        .body("Không thể xóa admin cuối cùng. Hệ thống cần ít nhất một admin.");
            }
            
            userService.deleteUser(userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa người dùng thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định: " + e.getClass().getSimpleName();
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }
}

