package com.counter.text.service;

import com.counter.text.model.CsvTextCountResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileHandlerService {

    void writeParagraphTextFileToUploads(MultipartFile paragraphFile);

    File writeCsvFileToDownloads(List<CsvTextCountResponse> result) throws Exception;
}
