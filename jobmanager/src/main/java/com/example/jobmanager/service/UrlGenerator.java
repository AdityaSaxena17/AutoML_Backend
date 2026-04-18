package com.example.jobmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.jobmanager.dto.JobUploadDto;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlGenerator {

    private final S3Presigner s3Presigner;
    
    private static final String BUCKET_NAME = "glassbox-workspace";

    public JobUploadDto generateUploadUrl(String fileName) {

        String jobId=UUID.randomUUID().toString();

        String s3Key = "datasets/" + jobId + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(s3Key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30)) 
                .putObjectRequest(putObjectRequest)
                .build();


        JobUploadDto dto=new JobUploadDto();        

        dto.setJobid(jobId);
        dto.setUrl(s3Presigner.presignPutObject(presignRequest).url().toString());
        return dto;

    }
}