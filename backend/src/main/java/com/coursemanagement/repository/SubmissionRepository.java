package com.coursemanagement.repository;

import com.coursemanagement.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    List<Submission> findByAssignmentId(Integer assignmentId);
    List<Submission> findByStudentId(Integer studentId);
    Optional<Submission> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);
    boolean existsByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);
}

