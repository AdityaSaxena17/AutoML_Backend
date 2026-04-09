package com.example.instantiationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;

@Configuration
public class AwsConfig {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    public AwsConfig(
            @Value("${ACCESS_KEY}") String accessKey,
            @Value("${SECRET_KEY}") String secretKey,
            @Value("${REGION}") String region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }

    @Bean
    public EcsClient ecsClient() { // Method name should be camelCase (ecsClient)
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return EcsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}