package com.counter.text.service;

import com.counter.text.dao.TextCounterDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.counter.text.util.Constants.TOPIC;

@Slf4j
@Service
public class ParagraphLineProducerImpl implements ParagraphLineProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TextCounterDatabase textCounterDatabase;

    @Override
    public void loadParagraphLines(String filePath) throws IOException {
        textCounterDatabase.clear();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(this::sendMessage);
        }
    }

    private void sendMessage(String message) {
        log.info(String.format("#### -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, message);
    }

}
