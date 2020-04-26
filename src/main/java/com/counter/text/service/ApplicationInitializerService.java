package com.counter.text.service;

import com.counter.text.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ApplicationInitializerService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ParagraphLineProducer paragraphLineProducer;

    @EventListener(ApplicationReadyEvent.class)
    public void loadParagraphTextAfterStartup() throws IOException {
        paragraphLineProducer.loadParagraphLines(
                String.format(
                        "%s%s",
                        applicationProperties.getUploadDirectory(),
                        applicationProperties.getUploadFleName()));
    }
}
