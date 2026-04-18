package com.example.jobmanager.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.jobmanager.dto.JobSubmissionDto;
import com.example.jobmanager.dto.JobUploadDto;
import com.example.jobmanager.dto.LambdaDto;
import com.example.jobmanager.service.JobCoordiantionService;
import com.example.jobmanager.service.UrlGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequiredArgsConstructor
public class JobCoordinationController {

    private final JobCoordiantionService jobCoordiantionService;
    private final UrlGenerator urlGenerator;


    @PostMapping("/submitJob")
    public void submitJob(@RequestBody JobSubmissionDto dto) {
        jobCoordiantionService.submitJob(dto);
    }

    @PostMapping("/sendJobStatus")
    public void sendJobStatus(@RequestBody LambdaDto dto) {
        jobCoordiantionService.sendJobStatus(dto);
    }

    @GetMapping("/getUploadUrl")
    public JobUploadDto getUploadUrl(@RequestParam("fileName") String fileName) {
        return urlGenerator.generateUploadUrl(fileName);
    }
    
    
}
