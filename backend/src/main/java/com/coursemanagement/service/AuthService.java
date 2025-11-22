package com.coursemanagement.service;

import com.coursemanagement.dto.LoginResponse;
import com.coursemanagement.dto.RegisterRequest;
import com.coursemanagement.entity.Student;
import com.coursemanagement.entity.User;
import com.coursemanagement.repository.StudentRepository;
import com.coursemanagement.repository.UserRepository;
import com.coursemanagement.util.JwtUtil;
import com.coursemanagement.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) throws Exception {
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("Tên đăng nhập không tồn tại"));
        
        // Debug: Log để kiểm tra
        System.out.println("Login attempt - Username: " + username);
        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Input password hash: " + PasswordHasher.hashPassword(password));
        
        if (!PasswordHasher.verifyPassword(password, user.getPassword())) {
            throw new Exception("Mật khẩu không đúng");
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        String roleStr = user.getRole().toString();
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), roleStr);
        String fullName = getFullName(roleStr, user.getReferenceId());
        
        return new LoginResponse(token, user.getUsername(), roleStr, fullName, user.getUserId());
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) throws Exception {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email đã được sử dụng");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email đã được sử dụng");
        }
        
        // Create student
        Student student = new Student();
        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth() != null ? 
                              request.getDateOfBirth().toLocalDate() : null);
        student.setAddress(request.getAddress());
        student.setEnrollmentDate(LocalDate.now());
        student.setCreatedAt(LocalDateTime.now());
        student = studentRepository.save(student);
        
        // Create user account
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordHasher.hashPassword(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(User.UserRole.STUDENT);
        user.setReferenceId(student.getStudentId());
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), 
                                            user.getRole().toString());
        
        return new LoginResponse(token, user.getUsername(), user.getRole().toString(), 
                                student.getFullName(), user.getUserId());
    }

    private String getFullName(String role, Integer referenceId) {
        if (referenceId == null || referenceId == 0) {
            return "Admin";
        }
        
        if ("STUDENT".equalsIgnoreCase(role)) {
            return studentRepository.findById(referenceId)
                .map(Student::getFullName)
                .orElse("Student");
        }
        
        // For instructor, we would need InstructorRepository
        return "User";
    }
}

