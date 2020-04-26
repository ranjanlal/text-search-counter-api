package com.counter.text.service;

import com.counter.text.dao.TextCounterDatabase;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class TextCounterServiceImplTest {

    @TestConfiguration
    static class TextCounterServicesImplTestContextConfiguration {
        @Bean
        public TextCounterService textCounterService() {
            return new TextCounterServiceImpl();
        }
    }

    @Autowired
    private TextCounterService textCounterService;

    @MockBean
    private TextCounterDatabase textCounterDatabase;

    @MockBean
    private FileHandlerService fileHandlerService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void givenEmptyDatabase_whenSearchForCountsForAGivenListOfWords_thenReturnMapWithZeroCounts() {

        // given
        when(textCounterDatabase.getCount(any())).thenReturn(0L);

        // when
        Map<String, Long> wordCounts = textCounterService.searchCountsFor(List.of("abc", "xyz", "pqr"));

        // then
        assertThat(wordCounts.size(), is(3));
        assertThat(wordCounts.get("abc"), is(0L));
        assertThat(wordCounts.get("xyz"), is(0L));
        assertThat(wordCounts.get("pqr"), is(0L));
    }

    @Test
    public void givenEmptyDatabase_whenSearchForTopNWordCounts_thenReturnEmptyOutputCSVFile() throws Exception {

        setUpEmptyTemporaryFileFixture();

        // when
        File csvReportFile = textCounterService.findTopWordCounts(5);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());
        assertThat(lines.size(), CoreMatchers.is(0));
    }

    @Test
    public void givenWordCountsInDatabase_whenSearchForCountsForAGivenListOfWords_thenReturnMapWithCounts() {

        // given
        when(textCounterDatabase.getCount(any())).thenReturn(0L);
        when(textCounterDatabase.getCount("xyz")).thenReturn(1L);
        when(textCounterDatabase.getCount("pqr")).thenReturn(2L);

        // when
        Map<String, Long> wordCounts = textCounterService.searchCountsFor(List.of("abc", "xyz", "pqr"));

        // then
        assertThat(wordCounts.size(), is(3));
        assertThat(wordCounts.get("abc"), is(0L));
        assertThat(wordCounts.get("xyz"), is(1L));
        assertThat(wordCounts.get("pqr"), is(2L));
    }

    @Test
    public void givenSomeRecordsInDatabase_whenSearchForTopNWordCounts_thenReturnTheOutputCSVFile() throws Exception {

        setUpTemporaryFileFixture();

        // when
        File csvReportFile = textCounterService.findTopWordCounts(5);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), CoreMatchers.is(3));
        assertThat(lines.get(0), CoreMatchers.is("abc|20"));
        assertThat(lines.get(1), CoreMatchers.is("xyz|10"));
        assertThat(lines.get(2), CoreMatchers.is("pqr|5"));

    }

    private void setUpTemporaryFileFixture() throws Exception {
        final File tempFile = setUpEmptyTemporaryFileFixture();
        FileUtils.write(tempFile,
                "abc|20\n"
                        + "xyz|10\n"
                        + "pqr|5\n", Charset.defaultCharset());
        when(fileHandlerService.writeCsvFileToDownloads(anyList())).thenReturn(tempFile);
    }

    private File setUpEmptyTemporaryFileFixture() throws Exception {
        final File tempFile = temporaryFolder.newFile("Output.csv");
        when(fileHandlerService.writeCsvFileToDownloads(anyList())).thenReturn(tempFile);
        return tempFile;
    }
}