package com.example.jobmanager.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.jobmanager.dto.JobSubmissionDto;
import com.example.jobmanager.dto.LambdaDto;
import com.example.jobmanager.service.JobCoordiantionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class JobCoordinationController {

    private final JobCoordiantionService jobCoordiantionService;


    @PostMapping("/submitJob")
    public void submitJob(@RequestBody JobSubmissionDto dto) {
        jobCoordiantionService.submitJob(dto);
    }

    @PostMapping("/sendJobStatus")
    public void sendJobStatus(@RequestBody LambdaDto dto) {
        jobCoordiantionService.sendJobStatus(dto);
    }
    
}
