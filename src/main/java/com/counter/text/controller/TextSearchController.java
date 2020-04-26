package com.counter.text.controller;

import com.counter.text.model.ApiResponse;
import com.counter.text.model.TextSearchPayload;
import com.counter.text.model.JsonTextCountResponse;
import com.counter.text.service.ApplicationInitializerService;
import com.counter.text.service.FileHandlerService;
import com.counter.text.service.TextCounterService;
import com.counter.text.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/counter-api")
public class TextSearchController {

    @Autowired
    Messages messages;

    @Autowired
    private ApplicationInitializerService applicationInitializerService;

    @Autowired
    private TextCounterService textCounterService;

    @Autowired
    private FileHandlerService fileHandlerService;

    /**
     * Welcome API
     *
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<ApiResponse> welcome() {
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        HttpStatus.OK,
                        messages.get("welcome.message"),
                        Optional.empty()));
    }

    /**
     * Search count for each of the texts (strings) input as a list
     *
     * @param payload
     * @return JSON with counts of each string found in the given paragraph
     */
    @PostMapping("/search")
    public ResponseEntity<JsonTextCountResponse> searchTextCounts(@RequestBody TextSearchPayload payload) {
        try {

            JsonTextCountResponse response = new JsonTextCountResponse(
                    textCounterService.searchCountsFor(payload.getSearchText()).entrySet());

            return ResponseEntity.ok().body(response);

        } catch (Exception ex) {
            log.error("Unable to upload file : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        }
    }

    /**
     * Search top N texts (strings)
     *
     * Generate report (in CSV format) for top N texts (strings) in the given paragraph
     *
     * @param numRecords
     * @return CSV format counts of top N texts in the paragraph
     */
    @GetMapping(value = "/top/{numRecords}", produces = "text/csv")
    public ResponseEntity reportCsv(@PathVariable(value = "numRecords") int numRecords) {

        try {

            File csvFile = textCounterService.findTopWordCounts(numRecords);

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            String.format("attachment; filename=%s", csvFile.getName()))
                    .contentLength(csvFile.length())
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new FileSystemResource(csvFile));

        } catch (Exception ex) {
            log.error("Unable to generate report : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate report", ex);
        }
    }

    /**
     * Upload Input paragraph file
     *
     * @param file - paragraph txt file
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> transactions(@RequestParam("file") MultipartFile file) {

        try {

            // upload paragraph file to upload directory
            fileHandlerService.writeParagraphTextFileToUploads(file);

            // load text and counts into database
            applicationInitializerService.loadParagraphTextAfterStartup();

            return ResponseEntity.ok()
                    .body(new ApiResponse(
                            HttpStatus.OK,
                            "File uploaded successfully",
                            Optional.empty()));

        } catch (Exception ex) {
            log.error("Unable to upload file : ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to upload file", ex);
        }
    }

}
