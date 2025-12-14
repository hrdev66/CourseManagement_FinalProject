package com.coursemanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "period_courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"period_id", "course_id"})
})
public class PeriodCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "period_id", nullable = false)
    private Integer periodId;
    
    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    // Constructors
    public PeriodCourse() {
    }

    public PeriodCourse(Integer periodId, Integer courseId) {
        this.periodId = periodId;
        this.courseId = courseId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}

