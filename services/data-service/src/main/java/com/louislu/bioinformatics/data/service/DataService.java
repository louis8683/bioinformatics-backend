package com.louislu.bioinformatics.data.service;

import com.louislu.bioinformatics.data.dto.DataEntryRequest;
import com.louislu.bioinformatics.data.dto.DataEntryResponse;
import com.louislu.bioinformatics.data.dto.SessionRequest;
import com.louislu.bioinformatics.data.dto.SessionResponse;
import com.louislu.bioinformatics.data.exception.IllegalRequestArgumentException;
import com.louislu.bioinformatics.data.exception.InvalidSessionException;
import com.louislu.bioinformatics.data.exception.ResourceNotFoundException;
import com.louislu.bioinformatics.data.model.DataEntry;
import com.louislu.bioinformatics.data.model.Session;
import com.louislu.bioinformatics.data.repository.DataEntryRepository;
import com.louislu.bioinformatics.data.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataEntryRepository dataEntryRepository;
    private final SessionRepository sessionRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    // CREATE

    public Long createSession(SessionRequest sessionRequest) {
        Session session = sessionRequest.toSession();
        Session savedSession = sessionRepository.save(session);
        return savedSession.getId();
    }

    public DataEntryResponse save(DataEntryRequest dataEntryRequest) {
        if (!sessionRepository.existsById(dataEntryRequest.sessionId())) {
            throw new InvalidSessionException("Invalid session ID: " + dataEntryRequest.sessionId());
        }

        Session session = sessionRepository.getReferenceById(dataEntryRequest.sessionId());
        DataEntry dataEntry = dataEntryRequest.toDataEntry(session);

        // Update the session start and end time
        OffsetDateTime currentTimestamp = dataEntry.getTimestamp();
        if (session.getStartTimestamp() == null || currentTimestamp.isBefore(session.getStartTimestamp())) {
            session.setStartTimestamp(currentTimestamp);
        }
        if (session.getEndTimestamp() == null || currentTimestamp.isAfter(session.getEndTimestamp())) {
            session.setEndTimestamp(currentTimestamp);
        }

        return DataEntryResponse.from(dataEntryRepository.save(dataEntry));
    }

    public List<Long> batchSave(List<DataEntryRequest> dataEntryRequests) {
        logger.info("batchSave start");

        if (dataEntryRequests.isEmpty()) {
            throw new IllegalRequestArgumentException("No data entries provided");
        }

        Long sessionId = dataEntryRequests.getFirst().sessionId();
        if (sessionId == null || !sessionRepository.existsById(sessionId)) {
            throw new InvalidSessionException("Invalid session ID: " + sessionId);
        }

        // Verify all entries belong to the same session
        boolean allSameSession = dataEntryRequests.stream()
                .allMatch(request -> sessionId.equals(request.sessionId()));
        if (!allSameSession) {
            throw new InvalidSessionException("All entries must belong to the same session ID: " + sessionId);
        }

        // Fetch the session proxy
        Session session = sessionRepository.getReferenceById(sessionId);

        // Convert each DTO to entity and associate it with the session
        List<DataEntry> dataEntries = dataEntryRequests.stream()
                .map(request -> request.toDataEntry(session))
                .toList();

        // Update the session start and end time based on the entries
        OffsetDateTime minTimestamp = dataEntries.stream()
                .map(DataEntry::getTimestamp)
                .max(OffsetDateTime::compareTo)
                .orElseThrow();
        OffsetDateTime maxTimestamp = dataEntries.stream()
                .map(DataEntry::getTimestamp)
                .min(OffsetDateTime::compareTo)
                .orElseThrow();

        if (session.getStartTimestamp() == null || minTimestamp.isBefore(session.getStartTimestamp())) {
            session.setStartTimestamp(minTimestamp);
        }
        if (session.getEndTimestamp() == null || maxTimestamp.isAfter(session.getEndTimestamp())) {
            session.setEndTimestamp(maxTimestamp);
        }

        // Save all entries
        List<DataEntry> savedEntries = dataEntryRepository.saveAll(dataEntries);

        return savedEntries.stream()
                .map(DataEntry::getId)
                .toList();
    }

    // READ

    public DataEntryResponse getEntry(Long entryId) {
        return DataEntryResponse.from(dataEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid entry ID: " + entryId)));
    }

    public List<DataEntryResponse> getEntriesFromSession(Long sessionId) {
        return dataEntryRepository.findAllBySessionId(sessionId).stream()
                .map(DataEntryResponse::from)
                .toList();
    }

    public Page<DataEntryResponse> getAllEntries(Pageable pageable) {
        return dataEntryRepository.findAll(pageable)
                .map(DataEntryResponse::from);
    }

    public SessionResponse getSession(Long sessionId) {
        return SessionResponse.from(sessionRepository.findById(sessionId).orElseThrow(
                () -> new InvalidSessionException("Invalid session ID: " + sessionId)
        ));
    }

    public List<SessionResponse> getSessionsFromUserId(String userId) {
        return sessionRepository.findAllByUserId(userId).stream()
                .map(SessionResponse::from)
                .toList();
    }

    // UPDATE

    public void updateSession(Long sessionId, SessionRequest sessionRequest) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new InvalidSessionException("Invalid session ID: " + sessionId);
        }

        // Fetch the session proxy
        Session session = sessionRepository.getReferenceById(sessionId);

        // Update the non-null fields
        if (sessionRequest.userId() != null) {
            session.setUserId(sessionRequest.userId());
        }
        if (sessionRequest.groupId() != null) {
            // TODO: make sure that this is a valid groupId to add to
            session.setGroupId(sessionRequest.groupId());
        }
        if (sessionRequest.sensorMac() != null) {
            session.setSensorMac(sessionRequest.sensorMac());
        }
        if (sessionRequest.description() != null) {
            session.setDescription(sessionRequest.description());
        }

        sessionRepository.save(session);
    }

    // DELETE

    public void deleteEntry(Long entryId) {
        if (!dataEntryRepository.existsById(entryId)) {
            throw new ResourceNotFoundException("Invalid entry ID: " + entryId);
        }

        dataEntryRepository.deleteById(entryId);
    }

    public void deleteSession(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new InvalidSessionException("Invalid session ID: " + sessionId);
        }

        // Delete all entries associated with the session
        List<DataEntry> entries = dataEntryRepository.findAllBySessionId(sessionId);
        dataEntryRepository.deleteAll(entries);

        // Delete the session
        sessionRepository.deleteById(sessionId);
    }


}
