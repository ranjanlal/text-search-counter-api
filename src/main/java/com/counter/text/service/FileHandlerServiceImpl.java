package com.counter.text.service;

import com.counter.text.config.ApplicationProperties;
import com.counter.text.model.CsvTextCountResponse;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ParagraphLineProducer paragraphLineProducer;

    @Override
    public void writeParagraphTextFileToUploads(MultipartFile paragraphFile) {

        String uploadDirectory = applicationProperties.getUploadDirectory();

        Path fileStorageLocation = Paths.get(uploadDirectory).toAbsolutePath().normalize();

        // Normalize file name
        String fileName = StringUtils.cleanPath(applicationProperties.getUploadFleName());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = fileStorageLocation.resolve(fileName);

            Files.copy(paragraphFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }

    }


    @Override
    public File writeCsvFileToDownloads(List<CsvTextCountResponse> result) throws Exception {

        String fileName = String.format("%s%s",
                applicationProperties.getDownloadDirectory(),
                applicationProperties.getDownloadFleName());

        Writer writer = new FileWriter(fileName);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator('|')
                .build();
        beanToCsv.write(result);
        writer.close();

        return new File(fileName);
    }


}
