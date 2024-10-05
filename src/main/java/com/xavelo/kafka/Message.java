package com.xavelo.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    @JsonProperty
    private String key;
    @JsonProperty
    private String value;
}

