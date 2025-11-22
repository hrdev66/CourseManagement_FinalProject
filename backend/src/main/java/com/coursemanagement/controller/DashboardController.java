package com.coursemanagement.controller;

import com.coursemanagement.repository.CourseRepository;
import com.coursemanagement.repository.StudentRepository;
import com.coursemanagement.repository.AssignmentRepository;
import com.coursemanagement.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalAssignments", assignmentRepository.count());
        stats.put("totalEnrollments", enrollmentRepository.count());
        return ResponseEntity.ok(stats);
    }
}

