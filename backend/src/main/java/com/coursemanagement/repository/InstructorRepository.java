package com.coursemanagement.repository;

import com.coursemanagement.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    Optional<Instructor> findByEmail(String email);
    boolean existsByEmail(String email);
}

