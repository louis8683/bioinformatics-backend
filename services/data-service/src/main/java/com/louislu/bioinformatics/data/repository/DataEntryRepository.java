package com.louislu.bioinformatics.data.repository;

import com.louislu.bioinformatics.data.model.DataEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataEntryRepository extends JpaRepository<DataEntry, Long> {
    List<DataEntry> findAllBySessionId(Long sessionId);
}
