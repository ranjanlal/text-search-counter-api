package com.counter.text.service;

import java.io.IOException;

public interface ParagraphLineProducer {

    void loadParagraphLines(String filePath) throws IOException;

}