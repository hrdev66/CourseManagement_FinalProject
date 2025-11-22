package com.coursemanagement.service;

import com.coursemanagement.entity.Course;
import com.coursemanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer id, Course courseDetails) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        
        course.setCourseName(courseDetails.getCourseName());
        course.setCourseCode(courseDetails.getCourseCode());
        course.setDescription(courseDetails.getDescription());
        course.setInstructorId(courseDetails.getInstructorId());
        course.setDurationWeeks(courseDetails.getDurationWeeks());
        course.setPrice(courseDetails.getPrice());
        course.setMaxStudents(courseDetails.getMaxStudents());
        course.setStatus(courseDetails.getStatus());
        
        return courseRepository.save(course);
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByInstructor(Integer instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> getCoursesByStatus(Course.CourseStatus status) {
        return courseRepository.findByStatus(status);
    }
}

