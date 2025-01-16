package com.louislu.bioinformatics.data.controller;

import com.louislu.bioinformatics.data.dto.DataEntryRequest;
import com.louislu.bioinformatics.data.dto.DataEntryResponse;
import com.louislu.bioinformatics.data.dto.SessionRequest;
import com.louislu.bioinformatics.data.dto.SessionResponse;
import com.louislu.bioinformatics.data.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    // CREATE

    @PostMapping("/createSession")
    public ResponseEntity<Long> createSession(@RequestBody SessionRequest sessionRequest) {
        Long sessionId = dataService.createSession(sessionRequest);
        return ResponseEntity.ok(sessionId);
    }

    @PostMapping("/entry")
    public ResponseEntity<DataEntryResponse> save(@RequestBody DataEntryRequest dataEntryRequest) {
        DataEntryResponse savedEntry = dataService.save(dataEntryRequest);
        URI location = URI.create("/api/data/" + savedEntry.id());
        return ResponseEntity.created(location).body(savedEntry);
    }

    @PostMapping("/entry/batchSave")
    public ResponseEntity<List<Long>> batchSave(@RequestBody List<DataEntryRequest> dataEntryRequests) {
        return ResponseEntity.ok(dataService.batchSave(dataEntryRequests));
    }

    // READ

    @GetMapping("/entry/{entryId}")
    public ResponseEntity<DataEntryResponse> getEntry(@PathVariable Long entryId) {
        DataEntryResponse dataEntryResponse = dataService.getEntry(entryId);
        return ResponseEntity.ok(dataEntryResponse);
    }

    @GetMapping("/entry/session/{sessionId}")
    public ResponseEntity<List<DataEntryResponse>> getEntriesFromSession(@PathVariable Long sessionId) {
        List<DataEntryResponse> entries = dataService.getEntriesFromSession(sessionId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entry/all")
    public ResponseEntity<Page<DataEntryResponse>> getAllEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DataEntryResponse> entries = dataService.getAllEntries(PageRequest.of(page, size));
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        SessionResponse sessionResponse = dataService.getSession(sessionId);
        return ResponseEntity.ok(sessionResponse);
    }

    @GetMapping("/session/user/{userId}")
    public ResponseEntity<List<SessionResponse>> getSessions(@PathVariable String userId) {
        List<SessionResponse> sessions = dataService.getSessionsFromUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/session/all")
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<SessionResponse> sessions = dataService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    // UPDATE

    @PutMapping("/session/{sessionId}")
    public ResponseEntity<Void> updateSession(
            @PathVariable Long sessionId,
            @RequestBody SessionRequest sessionRequest) {
        dataService.updateSession(sessionId, sessionRequest);
        return ResponseEntity.noContent().build();
    }

    // DELETE

    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId) {
        dataService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        dataService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
