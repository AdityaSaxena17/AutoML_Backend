package com.example.jobmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class S3Config{

    private final String accessKey;
    private final String secretKey;
    private final String region;

    S3Config(@Value("${ACCESS_KEY:}")String accessKey,@Value("${SECRET_KEY:}")String secretKey,@Value("${REGION:}")String region)
    {  
        this.accessKey=accessKey;
        this.secretKey=secretKey;
        this.region=region;
    }

        @Bean
        public S3Presigner s3Presigner() {
            AwsBasicCredentials credentials=AwsBasicCredentials.create(accessKey, secretKey);
            return S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
    }

    
                
                
                