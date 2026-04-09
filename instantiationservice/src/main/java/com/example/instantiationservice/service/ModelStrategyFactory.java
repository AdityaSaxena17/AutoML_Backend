package com.example.instantiationservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class ModelStrategyFactory {

    private Map<String,ModelStrategy> modelStrategies=new HashMap<>();

    public ModelStrategyFactory(List<ModelStrategy> strategies){
        for(ModelStrategy modelStrategy:strategies){
            modelStrategies.put(modelStrategy.getModel(), modelStrategy);
        }
    }

    public ModelStrategy getModel(String modeltype){
        ModelStrategy model=modelStrategies.get(modeltype);
        return model;
    }


}
