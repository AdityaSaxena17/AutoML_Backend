package com.example.stateservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.stateservice.Model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job,String>{

    Optional<Job> findByJobidAndUserid(String jobid, String userid);

}
