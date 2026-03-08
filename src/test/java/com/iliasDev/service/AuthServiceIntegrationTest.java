package com.iliasDev.service;

import com.iliasDev.BaseIntegrationTest;
import com.iliasDev.config.TestHibernateConfig;
import com.iliasDev.exception.UserAlreadyRegisteredException;
import com.iliasDev.exception.UserNotFoundException;
import com.iliasDev.exception.WrongPasswordException;
import com.iliasDev.model.dto.AuthorizationRequest;
import com.iliasDev.model.dto.RegistrationRequest;
import com.iliasDev.model.entity.SessionEntity;
import com.iliasDev.model.entity.User;
import com.iliasDev.repository.SessionRepository;
import com.iliasDev.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class AuthServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AuthService authService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_ShouldCreateUser() {
        RegistrationRequest request = new RegistrationRequest("test", "pass", "pass");

        User user = authService.register(request);

        Optional<User> userOptional = userRepository.findByLogin("test");

        assertNotNull(user.getId());
        assertTrue(userOptional.isPresent());

    }

    @Test
    void register_duplicateLogin_shouldThrowException() {

        RegistrationRequest request =
                new RegistrationRequest("test", "pass", "pass");

        authService.register(request);

        assertThrows(
                UserAlreadyRegisteredException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void authorization_correctPassword_shouldReturnUser() {

        RegistrationRequest registration =
                new RegistrationRequest("test", "pass", "pass");

        authService.register(registration);

        AuthorizationRequest authRequest =
                new AuthorizationRequest("test", "pass");

        User user = authService.authorization(authRequest);

        assertEquals("test", user.getLogin());
    }

    @Test
    void authorization_wrongPassword_shouldThrowException() {

        RegistrationRequest registration =
                new RegistrationRequest("testUser", "123", "123");

        authService.register(registration);

        AuthorizationRequest authRequest =
                new AuthorizationRequest("testUser", "wrong");

        assertThrows(
                WrongPasswordException.class,
                () -> authService.authorization(authRequest)
        );
    }

    @Test
    void authorization_userNotFound_shouldThrowException() {

        AuthorizationRequest request =
                new AuthorizationRequest("unknown", "123");

        assertThrows(
                UserNotFoundException.class,
                () -> authService.authorization(request)
        );
    }

}
