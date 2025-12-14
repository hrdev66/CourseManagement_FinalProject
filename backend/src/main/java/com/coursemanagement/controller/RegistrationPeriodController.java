package com.coursemanagement.controller;

import com.coursemanagement.entity.Course;
import com.coursemanagement.entity.RegistrationPeriod;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.RegistrationPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registration-periods")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RegistrationPeriodController {

    @Autowired
    private RegistrationPeriodService periodService;

    @Autowired
    private CourseService courseService;

    // Lấy tất cả đợt đăng ký
    @GetMapping
    public ResponseEntity<List<RegistrationPeriod>> getAllPeriods() {
        periodService.refreshAllPeriodStatuses(); // Cập nhật status trước khi trả về
        return ResponseEntity.ok(periodService.getAllPeriods());
    }

    // Lấy đợt đăng ký theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPeriodById(@PathVariable Integer id) {
        return periodService.getPeriodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy các đợt đăng ký đang active
    @GetMapping("/active")
    public ResponseEntity<List<RegistrationPeriod>> getActivePeriods() {
        periodService.refreshAllPeriodStatuses();
        return ResponseEntity.ok(periodService.getActivePeriods());
    }

    // Tạo đợt đăng ký mới
    @PostMapping
    public ResponseEntity<?> createPeriod(@RequestBody RegistrationPeriod period) {
        try {
            RegistrationPeriod created = periodService.createPeriod(period);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật đợt đăng ký
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePeriod(@PathVariable Integer id, @RequestBody RegistrationPeriod period) {
        try {
            RegistrationPeriod updated = periodService.updatePeriod(id, period);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa đợt đăng ký
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePeriod(@PathVariable Integer id) {
        try {
            periodService.deletePeriod(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy các khóa học trong đợt đăng ký
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getPeriodCourses(@PathVariable Integer id) {
        try {
            List<Integer> courseIds = periodService.getCourseIdsByPeriod(id);
            List<Course> courses = courseIds.stream()
                    .map(courseId -> courseService.getCourseById(courseId).orElse(null))
                    .filter(course -> course != null)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật các khóa học trong đợt đăng ký
    @PutMapping("/{id}/courses")
    public ResponseEntity<?> updatePeriodCourses(@PathVariable Integer id, @RequestBody List<Integer> courseIds) {
        try {
            periodService.updatePeriodCourses(id, courseIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cập nhật danh sách khóa học thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy đợt đăng ký với chi tiết khóa học
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getPeriodDetails(@PathVariable Integer id) {
        try {
            RegistrationPeriod period = periodService.getPeriodById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
            
            List<Integer> courseIds = periodService.getCourseIdsByPeriod(id);
            List<Course> courses = courseIds.stream()
                    .map(courseId -> courseService.getCourseById(courseId).orElse(null))
                    .filter(course -> course != null)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("period", period);
            response.put("courses", courses);
            response.put("courseIds", courseIds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

