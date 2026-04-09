package com.example.jobmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LambdaDto {
    
    private String jobid;
    private String userid;
    private String status;
    private ResultsDto results;
    private String s3path;
    private String modeltype;

}
