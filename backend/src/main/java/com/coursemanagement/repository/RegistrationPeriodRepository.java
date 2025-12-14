package com.coursemanagement.repository;

import com.coursemanagement.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Integer> {
    
    // Tìm các đợt đăng ký đang active (theo ngày hiện tại)
    @Query("SELECT p FROM RegistrationPeriod p WHERE p.startDate <= :today AND p.endDate >= :today")
    List<RegistrationPeriod> findActivePeriods(LocalDate today);
    
    // Tìm các đợt đăng ký theo status
    List<RegistrationPeriod> findByStatus(RegistrationPeriod.PeriodStatus status);
    
    // Sắp xếp theo ngày bắt đầu
    List<RegistrationPeriod> findAllByOrderByStartDateDesc();
}

