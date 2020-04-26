package com.counter.text.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CsvTextCountResponse {

    @CsvBindByPosition(position = 0)
    private String word;

    @CsvBindByPosition(position = 1)
    private Long count;
}
