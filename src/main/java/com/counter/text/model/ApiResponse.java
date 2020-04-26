package com.counter.text.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {

    public final HttpStatus status;
    public final String message;
    public final Optional<List<String>> errors;

    @JsonCreator
    public ApiResponse(@JsonProperty("status") HttpStatus status,
                       @JsonProperty("message") String message,
                       @JsonProperty("errors") Optional<List<String>> errors
    ) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
