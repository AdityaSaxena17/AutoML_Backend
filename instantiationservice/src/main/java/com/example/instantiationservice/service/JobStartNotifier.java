package com.example.instantiationservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.instantiationservice.dto.JobRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobStartNotifier {

    private final KafkaTemplate<String,JobRequestDto> kafkaTemplate;

    public void notify(JobRequestDto dto){
        kafkaTemplate.send(dto.getStatus(),dto);
    }

}
