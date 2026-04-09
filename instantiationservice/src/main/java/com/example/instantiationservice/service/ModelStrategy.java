package com.example.instantiationservice.service;

import com.example.instantiationservice.dto.JobRequestDto;

public interface ModelStrategy {
    
    public String getModel();

    public void execute(JobRequestDto dto);

}
