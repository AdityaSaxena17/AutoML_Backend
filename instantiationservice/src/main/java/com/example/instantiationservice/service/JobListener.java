package com.example.instantiationservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.instantiationservice.dto.JobRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobListener {

    private final JobStartNotifier jobStartNotifier;
    private final ModelStrategyFactory modelStrategyFactory;

    @KafkaListener(topics = {"jobCreated"},groupId = "instantiation-group")
    public void initiateJob(JobRequestDto dto){
        dto.setStatus("jobStarted");
        jobStartNotifier.notify(dto);
        ModelStrategy model=modelStrategyFactory.getModel(dto.getModeltype());
        model.execute(dto);

    }
}
