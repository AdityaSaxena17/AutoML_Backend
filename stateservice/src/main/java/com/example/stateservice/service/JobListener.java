package com.example.stateservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.stateservice.Model.Job;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobListener {
    
    private final StateHandler stateHandler;

    @KafkaListener(topics = {"jobCompleted","jobFailed","jobStarted"})
    public void listen(Job job){
        stateHandler.updateJob(job);
    }

}
