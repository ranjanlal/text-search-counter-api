package com.counter.text.dao;

import java.util.Map;

public interface TextCounterDatabase {

    void save(Map<String, Long> wordsCountMap);

    Long getCount(String searchWord);

    Map<String, Long> getTopWords(int numRecords);

    void clear();

}
