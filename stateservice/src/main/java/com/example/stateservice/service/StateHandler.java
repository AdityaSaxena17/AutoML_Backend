package com.example.stateservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.stateservice.Model.Job;
import com.example.stateservice.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateHandler {

    private final  JobRepository stateRepository;

    public Job fetchJob(String userid,String jobid){
        
        Optional<Job> job=stateRepository.findByJobidAndUserid(jobid,userid);
        return job.orElse(new Job());
    }

    public void updateJob(Job job){
        stateRepository.save(job);
    }

}
