package com.coursemanagement.repository;

import com.coursemanagement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByCourseId(Integer courseId);
    List<Announcement> findByInstructorId(Integer instructorId);
    List<Announcement> findByCourseIdOrderByCreatedAtDesc(Integer courseId);
}

