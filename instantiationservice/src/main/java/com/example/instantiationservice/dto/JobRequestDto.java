package com.example.instantiationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDto {

    private String jobid;
    private String userid;
    private String status;
    private String datasetpath;
    private String modeltype;

}
