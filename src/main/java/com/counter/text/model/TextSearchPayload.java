package com.counter.text.model;

import lombok.Data;

import java.util.List;

@Data
public class TextSearchPayload {

    private List<String> searchText;

}
