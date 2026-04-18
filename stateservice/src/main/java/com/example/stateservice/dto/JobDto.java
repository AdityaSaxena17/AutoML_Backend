package com.example.stateservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDto {
    private String jobid;
    private String userid;
    private String status;
    private String modeltype;
    private Map<String, Object> results;

    private String modelDownloadUrl;
    private String resultsDownloadUrl;
}