package com.example.stateservice.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.stateservice.Model.Job;
import com.example.stateservice.dto.JobDto;
import com.example.stateservice.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateHandler {

    private final  JobRepository stateRepository;
    private final S3LinkService s3LinkService;

    public JobDto fetchJob(String userid, String jobid) {
        Optional<Job> jobOptional = stateRepository.findByJobidAndUserid(jobid, userid);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();

            JobDto jobDto = JobDto.builder()
                    .jobid(job.getJobid())
                    .userid(job.getUserid())
                    .status(job.getStatus())
                    .modeltype(job.getModeltype())
                    .results(job.getResults())
                    .build();

            if ("jobCompleted".equalsIgnoreCase(job.getStatus())) {
                Map<String, String> links = s3LinkService.generateJobDownloadLinks(jobid);
                jobDto.setModelDownloadUrl(links.get("modelUrl"));
                jobDto.setResultsDownloadUrl(links.get("resultsUrl"));
            }

            return jobDto;
        }

        return new JobDto(); 
    }
    
    public void updateJob(Job job){
        stateRepository.save(job);
    }

}
