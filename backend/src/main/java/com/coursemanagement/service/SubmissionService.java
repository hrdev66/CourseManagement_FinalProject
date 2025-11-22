package com.coursemanagement.service;

import com.coursemanagement.entity.Submission;
import com.coursemanagement.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {
    
    @Autowired
    private SubmissionRepository submissionRepository;

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> getSubmissionById(Integer id) {
        return submissionRepository.findById(id);
    }

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Submission updateSubmission(Integer id, Submission submissionDetails) {
        Submission submission = submissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp"));
        
        submission.setContent(submissionDetails.getContent());
        submission.setAttachment(submissionDetails.getAttachment());
        submission.setScore(submissionDetails.getScore());
        submission.setStatus(submissionDetails.getStatus());
        
        return submissionRepository.save(submission);
    }

    public void deleteSubmission(Integer id) {
        submissionRepository.deleteById(id);
    }

    public List<Submission> getSubmissionsByAssignment(Integer assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudent(Integer studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}

