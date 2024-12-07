package com.louislu.bioinformatics.data.repository;

import com.louislu.bioinformatics.data.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByUserId(String userId);
}
