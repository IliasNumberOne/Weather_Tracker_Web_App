package com.iliasDev.service;

import com.iliasDev.model.entity.SessionEntity;
import com.iliasDev.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public SessionEntity createSession(Long userId) {
        SessionEntity sessionEntity = new SessionEntity(userId, LocalDateTime.now().plusHours(1));
        return sessionRepository.save(sessionEntity);
    }

    @Transactional
    public Optional<SessionEntity> getValidSession(UUID sessionId) {
        Optional<SessionEntity> sessionEntityOptional = sessionRepository.findById(sessionId);

        if(sessionEntityOptional.isPresent() && sessionEntityOptional.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            return sessionEntityOptional;
        }

        sessionEntityOptional.ifPresent(sessionRepository::delete);
        return Optional.empty();
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(sessionRepository::delete);
    }

    @Transactional
    public void deleteOldSessions(Long userId) {
        sessionRepository.deleteSessionsByUserId(userId);
    }



    @Transactional
    public boolean isSessionValid(UUID sessionId) {
        return getValidSession(sessionId).isPresent();
    }
}
