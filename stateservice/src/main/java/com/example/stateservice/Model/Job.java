package com.example.stateservice.Model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @Column(name="job_id",unique = true)
    private String jobid;
    @Column(name="user_id")
    private String userid;
    private String status;
    @Column(name="s3_path")
    private String s3path;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode results;
    @Column(name="model_type")
    private String modeltype;

    
    
}
