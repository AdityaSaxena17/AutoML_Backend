package com.example.jobmanager.util;

import lombok.Getter;
import com.example.jobmanager.dto.ResultsDto;

@Getter
public class Job {
    
    private final String jobid;
    private final String userid;
    private final String status;
    private final String modeltype; 

    private final ResultsDto results;
    private final String s3path;
    private final String datasetpath;

    private Job(Builder builder) {
        this.jobid = builder.jobid;
        this.userid = builder.userid;
        this.status = builder.status;
        this.modeltype = builder.modeltype; 
        this.results = builder.results;
        this.s3path = builder.s3path;
        this.datasetpath = builder.datasetpath;
    }

    public static class Builder {
        private final String jobid;
        private final String userid;
        private final String status;
        private final String modeltype;

        private ResultsDto results;
        private String s3path;
        private String datasetpath;

        public Builder(String jobid, String userid, String status, String modeltype) {
            this.jobid = jobid;
            this.userid = userid;
            this.status = status;
            this.modeltype = modeltype;
        }

        public Builder results(ResultsDto results) {
            this.results = results;
            return this;
        }

        public Builder s3Path(String s3path) {
            this.s3path = s3path;
            return this;
        }

        public Builder datasetPath(String datasetpath) {
            this.datasetpath = datasetpath;
            return this;
        }

        public Job build() {
            return new Job(this); 
        }
    }
}