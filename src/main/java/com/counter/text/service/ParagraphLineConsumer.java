package com.counter.text.service;

import com.counter.text.dao.TextCounterDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

import static com.counter.text.util.Constants.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class ParagraphLineConsumer {

    @Autowired
    private TextCounterDatabase textCounterDatabase;

    @KafkaListener(topics = TOPIC, groupId = TOPIC_GROUP_ID)
    public void consumeMessage(final String message) {

        log.info(String.format("#### -> Consumed message -> %s", message));

        try {

            String[] words = message.split(MESSAGE_SPLIT_REGEX);

            Map<String, Long> wordsCountMap = Stream.of(words)
                    .filter(x -> !x.isEmpty())
                    .collect(groupingBy(identity(), counting()));

            textCounterDatabase.save(wordsCountMap);

        } catch (final Exception ex) {
            log.error("Error = " + ex.getMessage());
        }
    }
}

