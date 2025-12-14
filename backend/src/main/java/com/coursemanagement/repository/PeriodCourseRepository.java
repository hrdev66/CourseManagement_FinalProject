package com.coursemanagement.repository;

import com.coursemanagement.entity.PeriodCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodCourseRepository extends JpaRepository<PeriodCourse, Integer> {
    
    // Lấy các khóa học trong một đợt đăng ký
    List<PeriodCourse> findByPeriodId(Integer periodId);
    
    // Xóa tất cả khóa học của một đợt
    void deleteByPeriodId(Integer periodId);
    
    // Kiểm tra khóa học có trong đợt không
    boolean existsByPeriodIdAndCourseId(Integer periodId, Integer courseId);
}

