package com.coursemanagement.service;

import com.coursemanagement.entity.Announcement;
import com.coursemanagement.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    
    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Optional<Announcement> getAnnouncementById(Integer id) {
        return announcementRepository.findById(id);
    }

    public Announcement createAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    public Announcement updateAnnouncement(Integer id, Announcement announcementDetails) {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        
        announcement.setTitle(announcementDetails.getTitle());
        announcement.setContent(announcementDetails.getContent());
        announcement.setPriority(announcementDetails.getPriority());
        
        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Integer id) {
        announcementRepository.deleteById(id);
    }

    public List<Announcement> getAnnouncementsByCourse(Integer courseId) {
        return announcementRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    public List<Announcement> getAnnouncementsByInstructor(Integer instructorId) {
        return announcementRepository.findByInstructorId(instructorId);
    }
}

