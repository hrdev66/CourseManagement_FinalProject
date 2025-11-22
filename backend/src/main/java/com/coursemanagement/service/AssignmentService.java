package com.coursemanagement.service;

import com.coursemanagement.entity.Assignment;
import com.coursemanagement.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Optional<Assignment> getAssignmentById(Integer id) {
        return assignmentRepository.findById(id);
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Integer id, Assignment assignmentDetails) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));
        
        assignment.setTitle(assignmentDetails.getTitle());
        assignment.setDescription(assignmentDetails.getDescription());
        assignment.setDueDate(assignmentDetails.getDueDate());
        assignment.setMaxScore(assignmentDetails.getMaxScore());
        assignment.setAssignmentType(assignmentDetails.getAssignmentType());
        assignment.setStatus(assignmentDetails.getStatus());
        
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Integer id) {
        assignmentRepository.deleteById(id);
    }

    public List<Assignment> getAssignmentsByCourse(Integer courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }
}

