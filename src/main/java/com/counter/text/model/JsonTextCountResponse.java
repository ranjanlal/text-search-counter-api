package com.counter.text.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class JsonTextCountResponse {
    Set<Map.Entry<String, Long>> counts;
}
