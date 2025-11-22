package com.coursemanagement.repository;

import com.coursemanagement.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByCourseId(Integer courseId);
    List<Assignment> findByCourseIdAndStatus(Integer courseId, Assignment.AssignmentStatus status);
}

