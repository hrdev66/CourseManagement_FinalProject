package com.coursemanagement.service;

import com.coursemanagement.entity.Instructor;
import com.coursemanagement.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {
    
    @Autowired
    private InstructorRepository instructorRepository;

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> getInstructorById(Integer id) {
        return instructorRepository.findById(id);
    }

    public Instructor createInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Instructor updateInstructor(Integer id, Instructor instructorDetails) {
        Instructor instructor = instructorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên"));
        
        instructor.setFullName(instructorDetails.getFullName());
        instructor.setEmail(instructorDetails.getEmail());
        instructor.setPhone(instructorDetails.getPhone());
        instructor.setSpecialization(instructorDetails.getSpecialization());
        instructor.setBio(instructorDetails.getBio());
        
        return instructorRepository.save(instructor);
    }

    public void deleteInstructor(Integer id) {
        instructorRepository.deleteById(id);
    }
}

