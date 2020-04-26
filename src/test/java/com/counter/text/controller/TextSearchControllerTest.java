package com.counter.text.controller;

import com.counter.text.service.ApplicationInitializerService;
import com.counter.text.service.FileHandlerService;
import com.counter.text.service.TextCounterService;
import com.counter.text.util.Messages;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class TextSearchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    Messages messages;

    @MockBean
    private TextCounterService textCounterService;

    @MockBean
    private FileHandlerService fileHandlerService;

    @MockBean
    private ApplicationInitializerService applicationInitializerService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void givenAWelcomeMessage_whenGetRooAPICalled_thenReturnJsonObjectOfWelcome() throws Exception {

        // given
        given(messages.get("welcome.message")).willReturn("Welcome to API");

        // when then
        mvc.perform(
                get("/counter-api/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is("Welcome to API")));
    }

    @Test
    public void givenAParagraphFileLoadedInDatabaseWithWordCounts_whenSearchCountsForGivenListOfWords_thenReturnJsonArrayOfCounts() throws Exception {

        // given
        Map<String, Long> searchCounts = setUpWordCountsFor10SearchWords();

        given(textCounterService.searchCountsFor(anyList())).willReturn(searchCounts);

        String jsonPayload = "{\"searchText\":[\"paragraph\",\"a\",\"vel\",\"sit\",\"Duis\",\"Sed\",\"Donec\",\"Augue\",\"Pellentesque\",\"123\"]}";

        // when then
        mvc.perform(MockMvcRequestBuilders
                .post("/counter-api/search")
                .content(jsonPayload)
                .header("Authorization", "Basic dXNlcjpwd2Q=")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.counts").isArray())
                .andExpect(jsonPath("$.counts", hasSize(10)))
                .andExpect(jsonPath("$.counts[*].paragraph").value(75))
                .andExpect(jsonPath("$.counts[*].a").value(78))
                .andExpect(jsonPath("$.counts[*].Duis").value(11));
    }

    @Test
    public void givenOneRecordInDatabase_whenGetAllReport_thenReturnJsonArrayOfTransactionSummary() throws Exception {

        // given
        File csvFile = setUpTemporaryFileFixture();
        given(textCounterService.findTopWordCounts(anyInt())).willReturn(csvFile);

        final MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv", Charset.forName("utf-8"));

        // when then
        mvc.perform(
                get("/counter-api/top/{numRecords}", 5)
                        .header("Authorization", "Basic dXNlcjpwd2Q=")
                        .accept(MEDIA_TYPE_CSV))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").
                        value("abc|20\n" +
                                "xyz|10\n" +
                                "pqr|5"));
    }

    private Map<String, Long> setUpWordCountsFor10SearchWords() {
        return Map.of(
                "paragraph", 75L,
                "a", 78L,
                "vel", 0L,
                "sit", 0L,
                "Duis", 11L,
                "Sed", 0L,
                "Donec", 0L,
                "Augue", 0L,
                "Pellentesque", 0L,
                "123", 0L
        );
    }

    private File setUpTemporaryFileFixture() throws Exception {
        final File tempFile = setUpEmptyTemporaryFileFixture();
        FileUtils.write(tempFile,
                "abc|20\n"
                        + "xyz|10\n"
                        + "pqr|5\n", Charset.defaultCharset());
        when(fileHandlerService.writeCsvFileToDownloads(anyList())).thenReturn(tempFile);
        return tempFile;
    }

    private File setUpEmptyTemporaryFileFixture() throws Exception {
        final File tempFile = temporaryFolder.newFile("Output.csv");
        when(fileHandlerService.writeCsvFileToDownloads(anyList())).thenReturn(tempFile);
        return tempFile;
    }


}