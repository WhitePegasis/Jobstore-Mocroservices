package com.sourav.jobms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sourav.jobms.clients.CompanyClient;
import com.sourav.jobms.clients.ReviewClient;
import com.sourav.jobms.dto.JobDTO;
import com.sourav.jobms.external.Company;
import com.sourav.jobms.external.Review;
import com.sourav.jobms.mapper.JobMapper;
import com.sourav.jobms.model.Job;
import com.sourav.jobms.repository.JobRepository;
import com.sourav.jobms.service.JobService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class JobServiceImpl implements JobService {
	// private List<Job> jobs = new ArrayList<>();
	JobRepository jobRepository;
	
	int attempt = 0;

	@Autowired
	RestTemplate restTemplate;

	private CompanyClient companyClient;
	private ReviewClient reviewClient;

	public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient, ReviewClient reviewClient) {
		this.jobRepository = jobRepository;
		this.companyClient = companyClient;
		this.reviewClient = reviewClient;
	}

	@Override
//  @CircuitBreaker(name = "companyBreaker",
//          fallbackMethod = "companyBreakerFallback")
//	@Retry(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
	@RateLimiter(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
	public List<JobDTO> findAll() {
		System.out.println("Attempt: " + ++attempt);
		List<Job> jobs = jobRepository.findAll();
//		List<JobDTO> jobDTOS = new ArrayList<>();

		return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<String> companyBreakerFallback(Exception e) {
		List<String> list = new ArrayList<>();
		list.add("Dummy");
		return list;
	}

	private JobDTO convertToDto(Job job) {
		Company company = companyClient.getCompany(job.getCompanyId());
		List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

		JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job, company, reviews);
		// jobDTO.setCompany(company);

		return jobDTO;
	}

	@Override
	public void createJob(Job job) {
		jobRepository.save(job);
	}

	@Override
	public JobDTO getJobById(Long id) {
		Job job = jobRepository.findById(id).orElse(null);
		return convertToDto(job);
	}

	@Override
	public boolean deleteJobById(Long id) {
		try {
			jobRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean updateJob(Long id, Job updatedJob) {
		Optional<Job> jobOptional = jobRepository.findById(id);
		if (jobOptional.isPresent()) {
			Job job = jobOptional.get();
			job.setTitle(updatedJob.getTitle());
			job.setDescription(updatedJob.getDescription());
			job.setMinSalary(updatedJob.getMinSalary());
			job.setMaxSalary(updatedJob.getMaxSalary());
			job.setLocation(updatedJob.getLocation());
			jobRepository.save(job);
			return true;
		}
		return false;
	}
}
