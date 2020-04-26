package com.counter.text.service;

import com.counter.text.dao.TextCounterDatabase;
import com.counter.text.model.CsvTextCountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class TextCounterServiceImpl implements TextCounterService {

    @Autowired
    private FileHandlerService fileHandlerService;

    @Autowired
    private TextCounterDatabase textCounterDatabase;

    @Override
    public Map<String, Long> searchCountsFor(List<String> words) {
        return words
                .stream()
                .collect(toMap(identity(), word -> textCounterDatabase.getCount(word),
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public File findTopWordCounts(int numRecords) throws Exception {
        List<CsvTextCountResponse> result = textCounterDatabase.getTopWords(numRecords)
                .entrySet()
                .stream()
                .map(x -> new CsvTextCountResponse(x.getKey(), x.getValue()))
                .collect(toList());
        return fileHandlerService.writeCsvFileToDownloads(result);
    }
}

