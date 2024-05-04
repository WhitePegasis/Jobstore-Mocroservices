package com.sourav.jobms.service;

import java.util.List;

import com.sourav.jobms.dto.JobDTO;
import com.sourav.jobms.model.Job;

public interface JobService {
    List<JobDTO> findAll();
    void createJob(Job job);

    JobDTO getJobById(Long id);

    boolean deleteJobById(Long id);

    boolean updateJob(Long id, Job updatedJob);
}
