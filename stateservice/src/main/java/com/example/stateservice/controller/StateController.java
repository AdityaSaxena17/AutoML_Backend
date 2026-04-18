package com.example.stateservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.stateservice.Model.Job;
import com.example.stateservice.dto.JobDto;
import com.example.stateservice.service.StateHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
public class StateController {

    private final StateHandler stateHandler;

    @GetMapping("/user/{userid}/job/{jobid}")
    public ResponseEntity<JobDto> getState(@PathVariable("userid") String userid,
        @PathVariable("jobid") String jobid) {
        return ResponseEntity.ok().body(stateHandler.fetchJob(userid, jobid));
    }

    

    
}
