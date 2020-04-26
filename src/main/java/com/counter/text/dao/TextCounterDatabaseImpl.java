package com.counter.text.dao;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TextCounterDatabaseImpl implements TextCounterDatabase {

    private Map<String, Long> wordsCountDatabase;

    @Override
    public void save(Map<String, Long> wordsCountMap) {
        wordsCountMap
                .forEach(
                        (word, count) -> wordsCountDatabase.merge(word.toLowerCase(), count,
                                (prev, current) -> prev + current)
                );
    }

    @Override
    public Long getCount(String searchWord) {
        return wordsCountDatabase.containsKey(searchWord.toLowerCase())
                ? wordsCountDatabase.get(searchWord.toLowerCase())
                : 0L;
    }

    @Override
    public Map<String, Long> getTopWords(int numRecords) {
        Map<String, Long> topWords = wordsCountDatabase
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(numRecords)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                );
        return Collections.unmodifiableMap(topWords);
    }

    @Override
    public void clear() {
        wordsCountDatabase.clear();
    }
}
