package com.counter.text.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TextCounterService {

    Map<String, Long> searchCountsFor(List<String> words);

    File findTopWordCounts(int numRecords) throws Exception;

}
