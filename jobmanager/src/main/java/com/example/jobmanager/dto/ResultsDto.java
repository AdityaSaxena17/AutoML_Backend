package com.example.jobmanager.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResultsDto {
    private Map<String, Object> data;

    public ResultsDto() {
        this.data = new HashMap<>();
    }

    @JsonAnySetter
    public void setDynamicProperty(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        data.put(key, value);
    }
}
