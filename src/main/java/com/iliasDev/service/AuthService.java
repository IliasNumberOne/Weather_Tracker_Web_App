package com.iliasDev.service;

import com.iliasDev.exception.UserAlreadyRegisteredException;
import com.iliasDev.exception.UserNotFoundException;
import com.iliasDev.exception.WrongPasswordException;
import com.iliasDev.model.dto.AuthorizationRequest;
import com.iliasDev.model.dto.RegistrationRequest;
import com.iliasDev.model.entity.User;
import com.iliasDev.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(RegistrationRequest request) {
        User user = new User();
        user.setLogin(request.login());
        user.setPassword(BCrypt.hashpw(request.password(), BCrypt.gensalt()));

        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new UserAlreadyRegisteredException("User already registered");
        }
        userRepository.save(user);

        return user;
    }

    @Transactional
    public User authorization(AuthorizationRequest request) {
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }

        return user;
    }
}
