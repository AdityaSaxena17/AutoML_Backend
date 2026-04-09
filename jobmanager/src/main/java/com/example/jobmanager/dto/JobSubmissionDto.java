package com.example.jobmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSubmissionDto {

    private String userid;
    private String datasetpath;
    private String modeltype;
}
