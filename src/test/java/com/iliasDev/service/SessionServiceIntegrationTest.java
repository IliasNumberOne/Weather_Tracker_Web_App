package com.iliasDev.service;

import com.iliasDev.BaseIntegrationTest;
import com.iliasDev.model.dto.RegistrationRequest;
import com.iliasDev.model.entity.*;
import com.iliasDev.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceIntegrationTest extends BaseIntegrationTest {
    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AuthService authService;

    @Test
    void createSession_shouldPersistSession() {

        User user = authService.register(
                new RegistrationRequest("user", "123", "123")
        );

        SessionEntity session =
                sessionService.createSession(user.getId());

        Optional<SessionEntity> fromDb =
                sessionRepository.findById(session.getId());

        assertTrue(fromDb.isPresent());
    }

    @Test
    void isSessionValid_shouldReturnTrue() {

        User user = authService.register(
                new RegistrationRequest("user", "123", "123")
        );

        SessionEntity session =
                sessionService.createSession(user.getId());

        boolean valid = sessionService.isSessionValid(session.getId());

        assertTrue(valid);
    }

    @Test
    void deleteSession_shouldRemoveSession() {

        User user = authService.register(
                new RegistrationRequest("user", "123", "123")
        );

        SessionEntity session =
                sessionService.createSession(user.getId());

        sessionService.deleteSession(session.getId());

        Optional<SessionEntity> fromDb =
                sessionRepository.findById(session.getId());

        assertTrue(fromDb.isEmpty());
    }

    @Test
    void deleteOldSessions_shouldDeleteAllUserSessions() {

        User user = authService.register(
                new RegistrationRequest("user", "123", "123")
        );

        sessionService.createSession(user.getId());
        sessionService.createSession(user.getId());

        sessionService.deleteOldSessions(user.getId());

        List<SessionEntity> sessions =
                sessionRepository.findByUserId(user.getId());

        assertTrue(sessions.isEmpty());
    }

    @Test
    void getValidSession_shouldDeleteExpiredSession() {

        User user = authService.register(
                new RegistrationRequest("user", "123", "123")
        );

        SessionEntity session =
                sessionService.createSession(user.getId());

        session.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        Optional<SessionEntity> result =
                sessionService.getValidSession(session.getId());

        assertTrue(result.isEmpty());
    }
}
