package com.example.jobmanager.service;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.jobmanager.dto.JobSubmissionDto;
import com.example.jobmanager.dto.LambdaDto;
import com.example.jobmanager.util.Job;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobCoordiantionService {

    private final KafkaTemplate<String,Job> kafkaTemplate;
    
    public void submitJob(JobSubmissionDto dto){

        Job job=new Job.Builder(UUID.randomUUID().toString(),dto.getUserid(),"jobCreated",dto.getModeltype())
        .datasetPath(dto.getDatasetpath())
        .build();

        kafkaTemplate.send(job.getStatus(),job);
    }

    public void sendJobStatus(LambdaDto dto){

        Job job=new Job.Builder(dto.getJobid(), dto.getUserid(), dto.getStatus(),dto.getModeltype())
        .results(dto.getResults())
        .s3Path(dto.getS3path())
        .build();

        kafkaTemplate.send(job.getStatus(),job);

    }
}
