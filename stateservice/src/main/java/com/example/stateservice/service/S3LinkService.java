package com.example.stateservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3LinkService {

    private final S3Presigner s3Presigner;
    
    private static final String BUCKET_NAME = "glassbox-workspace";


    public Map<String, String> generateJobDownloadLinks(String jobId) {
        Map<String, String> links = new HashMap<>();

        String modelKey = "results/" + jobId + "/model.pkl";
        String resultsKey = "results/" + jobId + "/output.json";

        links.put("modelUrl", createPresignedGetUrl(modelKey));
        links.put("resultsUrl", createPresignedGetUrl(resultsKey));

        return links;
    }

    private String createPresignedGetUrl(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60)) 
                .getObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}