package com.counter.text.service;


import com.counter.text.config.ApplicationProperties;
import com.counter.text.model.CsvTextCountResponse;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileHandlerServiceImplTest {

    @TestConfiguration
    static class TextCounterServiceImplTestContextConfiguration {
        @Bean
        public FileHandlerService fileHandlerService() {
            return new FileHandlerServiceImpl();
        }
    }

    @Autowired
    private FileHandlerService fileHandlerService;

    @MockBean
    private ApplicationProperties applicationProperties;

    @MockBean
    private ParagraphLineProducer paragraphLineProducer;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void givenEmptyResponseTupleList_whenWriteCSVFileFromResponseTuples_thenReturnEmptyGeneratedFile() throws Exception {

        // given
        setUpOneWordCountRecordInResponseTuples();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(Collections.EMPTY_LIST);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(0));
    }

    @Test
    public void givenNullResponseTupleList_whenWriteCSVFileFromResponseTuples_thenReturnEmptyGeneratedFile() throws Exception {

        // given
        setUpOneWordCountRecordInResponseTuples();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(null);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(0));
    }

    @Test
    public void givenResponseTuple_whenWriteCSVFileFromResponseTuples_thenReturnGeneratedFile() throws Exception {

        // given
        List<CsvTextCountResponse> responseTuples = setUpOneWordCountRecordInResponseTuples();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(responseTuples);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("unique|10"));
    }


    @Test
    public void givenResponseTuple_whenWriteParagraphTextFileToUploads_thenVerifyGeneratedFile() throws Exception {

        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "user-file",
                "Upload.txt",
                "text/plain",
                "test data".getBytes());

        when(applicationProperties.getUploadDirectory()).thenReturn(temporaryFolder.getRoot().getAbsolutePath() + "/");
        when(applicationProperties.getUploadFleName()).thenReturn(mockMultipartFile.getOriginalFilename());
        doNothing().when(paragraphLineProducer).loadParagraphLines(anyString());

        // when
        fileHandlerService.writeParagraphTextFileToUploads(mockMultipartFile);

        // then
        File[] files = temporaryFolder.getRoot().listFiles();

        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("Upload.txt"));

        List<String> lines = FileUtils.readLines(files[0], Charset.defaultCharset());

        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("test data"));

    }


    private List<CsvTextCountResponse> setUpOneWordCountRecordInResponseTuples() throws IOException {

        List<CsvTextCountResponse> responseTuples = List.of(new CsvTextCountResponse("unique", 10L));

        when(applicationProperties.getDownloadDirectory()).thenReturn(temporaryFolder.newFolder("output").getAbsolutePath() + "/");
        when(applicationProperties.getDownloadFleName()).thenReturn("Output.csv");

        return responseTuples;
    }

}