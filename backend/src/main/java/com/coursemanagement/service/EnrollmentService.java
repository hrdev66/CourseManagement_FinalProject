package com.coursemanagement.service;

import com.coursemanagement.entity.Enrollment;
import com.coursemanagement.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment createEnrollment(Enrollment enrollment) {
        // Check if enrollment already exists
        if (enrollmentRepository.existsByStudentIdAndCourseId(
                enrollment.getStudentId(), enrollment.getCourseId())) {
            throw new RuntimeException("Sinh viên đã đăng ký khóa học này");
        }
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment updateEnrollment(Integer id, Enrollment enrollmentDetails) {
        Enrollment enrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký"));
        
        enrollment.setCompletionStatus(enrollmentDetails.getCompletionStatus());
        enrollment.setGrade(enrollmentDetails.getGrade());
        enrollment.setPaymentStatus(enrollmentDetails.getPaymentStatus());
        
        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Integer id) {
        enrollmentRepository.deleteById(id);
    }

    public List<Enrollment> getEnrollmentsByStudent(Integer studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Integer courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}

