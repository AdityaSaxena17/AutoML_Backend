package com.example.instantiationservice.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.instantiationservice.dto.JobRequestDto;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

@Service
@RequiredArgsConstructor
public class KMeansModelStrategy implements ModelStrategy {

    private final EcsClient ecsClient;

    @Override
    public void execute(JobRequestDto request) {
        System.out.println("--- Starting KMeans Instantiation for Job: " + request.getJobid() + " ---");

        try {
            String inputS3Path = request.getDatasetpath(); 
            String outputS3Prefix = String.format("s3://glassbox-workspace/results/%s/", request.getJobid());

            ContainerOverride containerOverride = ContainerOverride.builder()
                    .name("glassbox-worker") 
                    .environment(
                            KeyValuePair.builder().name("JOB_ID").value(request.getJobid()).build(),
                            KeyValuePair.builder().name("USER_ID").value(request.getUserid()).build(),
                            KeyValuePair.builder().name("INPUT_S3_PATH").value(inputS3Path).build(),
                            KeyValuePair.builder().name("OUTPUT_S3_PREFIX").value(outputS3Prefix).build(),
                            KeyValuePair.builder().name("MODEL_TYPE").value("KMeans").build() 
                    )
                    .build();

            RunTaskRequest runTaskRequest = RunTaskRequest.builder()
                    .cluster("glassbox-cluster") 
                    .taskDefinition("unified-ml-task") 
                    .launchType(LaunchType.FARGATE)
                    .networkConfiguration(NetworkConfiguration.builder()
                            .awsvpcConfiguration(AwsVpcConfiguration.builder()
                                    .subnets(List.of(
                                        "subnet-0660429ac2b53557c", 
                                        "subnet-0bc33068ddad4aa74", 
                                        "subnet-0bfbbac48ed905eb1"
                                    )) 
                                    .securityGroups("sg-094aaabfeace9707e") 
                                    .assignPublicIp(AssignPublicIp.ENABLED)
                                    .build())
                            .build())
                    .overrides(TaskOverride.builder()
                            .containerOverrides(containerOverride)
                            .build())
                    .build();

            RunTaskResponse response = ecsClient.runTask(runTaskRequest);
            
            if (response.hasTasks()) {
                String taskArn = response.tasks().get(0).taskArn();
                System.out.println("KMeans Fargate Task Launched Successfully. ARN: " + taskArn);
            } else {
                System.err.println("Task launch failed: " + response.failures().toString());
            }

        } catch (Exception e) {
            System.err.println("Error in KMeansModelStrategy: " + e.getMessage());
            throw new RuntimeException("Failed to spin up KMeans container", e);
        }
    }

    @Override
    public String getModel() {
        return "KMeans"; 
    }
}