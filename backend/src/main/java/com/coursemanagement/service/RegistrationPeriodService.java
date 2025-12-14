package com.coursemanagement.service;

import com.coursemanagement.entity.PeriodCourse;
import com.coursemanagement.entity.RegistrationPeriod;
import com.coursemanagement.repository.PeriodCourseRepository;
import com.coursemanagement.repository.RegistrationPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationPeriodService {

    @Autowired
    private RegistrationPeriodRepository periodRepository;

    @Autowired
    private PeriodCourseRepository periodCourseRepository;

    public List<RegistrationPeriod> getAllPeriods() {
        return periodRepository.findAllByOrderByStartDateDesc();
    }

    public Optional<RegistrationPeriod> getPeriodById(Integer id) {
        return periodRepository.findById(id);
    }

    public List<RegistrationPeriod> getActivePeriods() {
        return periodRepository.findActivePeriods(LocalDate.now());
    }

    @Transactional
    public RegistrationPeriod createPeriod(RegistrationPeriod period) {
        period.setCreatedAt(LocalDateTime.now());
        updatePeriodStatus(period);
        return periodRepository.save(period);
    }

    @Transactional
    public RegistrationPeriod updatePeriod(Integer id, RegistrationPeriod periodDetails) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));

        period.setPeriodName(periodDetails.getPeriodName());
        period.setDescription(periodDetails.getDescription());
        period.setStartDate(periodDetails.getStartDate());
        period.setEndDate(periodDetails.getEndDate());
        updatePeriodStatus(period);

        return periodRepository.save(period);
    }

    @Transactional
    public void deletePeriod(Integer id) {
        // Xóa các khóa học liên kết trước
        periodCourseRepository.deleteByPeriodId(id);
        periodRepository.deleteById(id);
    }

    // Cập nhật status dựa trên ngày
    private void updatePeriodStatus(RegistrationPeriod period) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(period.getStartDate())) {
            period.setStatus(RegistrationPeriod.PeriodStatus.upcoming);
        } else if (today.isAfter(period.getEndDate())) {
            period.setStatus(RegistrationPeriod.PeriodStatus.closed);
        } else {
            period.setStatus(RegistrationPeriod.PeriodStatus.active);
        }
    }

    // Lấy danh sách course IDs trong một đợt
    public List<Integer> getCourseIdsByPeriod(Integer periodId) {
        return periodCourseRepository.findByPeriodId(periodId)
                .stream()
                .map(PeriodCourse::getCourseId)
                .toList();
    }

    // Cập nhật các khóa học trong đợt đăng ký
    @Transactional
    public void updatePeriodCourses(Integer periodId, List<Integer> courseIds) {
        // Xóa các khóa học cũ
        periodCourseRepository.deleteByPeriodId(periodId);

        // Thêm các khóa học mới
        for (Integer courseId : courseIds) {
            PeriodCourse pc = new PeriodCourse(periodId, courseId);
            periodCourseRepository.save(pc);
        }
    }

    // Cập nhật status cho tất cả đợt đăng ký
    @Transactional
    public void refreshAllPeriodStatuses() {
        List<RegistrationPeriod> periods = periodRepository.findAll();
        for (RegistrationPeriod period : periods) {
            updatePeriodStatus(period);
            periodRepository.save(period);
        }
    }
}

