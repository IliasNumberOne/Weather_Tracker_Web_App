package com.iliasDev.controller;

import com.iliasDev.model.dto.AuthorizationRequest;
import com.iliasDev.model.dto.RegistrationRequest;
import com.iliasDev.model.entity.SessionEntity;
import com.iliasDev.model.entity.User;
import com.iliasDev.service.AuthService;
import com.iliasDev.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final SessionService sessionService;

    public AuthController(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
    }

    //---SIGN-UP-------
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest("", "", ""));
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String register(@Valid @ModelAttribute RegistrationRequest registrationRequest,
                           BindingResult bindingResult,
                           HttpServletResponse response,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return ("sign-up");
        }

        try{
            User user = authService.register(registrationRequest);
            SessionEntity session = sessionService.createSession(user.getId());

            Cookie cookie = new Cookie("SESSION_ID", session.getId().toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60*60);
            response.addCookie(cookie);

            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "sign-up";
        }
    }


    //---SIGN-IN-------
    @GetMapping("/sign-in")
    public String signInForm(Model model) {
        model.addAttribute("authorizationRequest", new AuthorizationRequest("", ""));
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String login(@Valid @ModelAttribute AuthorizationRequest authorizationRequest,
                         BindingResult bindingResult,
                         HttpServletResponse response,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "sign-in";
        }

        try{
            User user = authService.authorization(authorizationRequest);
            SessionEntity session = sessionService.createSession(user.getId());

            Cookie cookie = new Cookie("SESSION_ID", session.getId().toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60*60);
            response.addCookie(cookie);

            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "sign-in";
        }

    }


    //---SIGN-OUT-------
    @PostMapping("/sign-out")
    public String signOut(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                          HttpServletResponse response) {
        if (sessionId != null) {
            try {
                UUID uuid = UUID.fromString(sessionId);
                sessionService.deleteSession(uuid);

                Cookie cookie = new Cookie("SESSION_ID", null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return "redirect:/auth/sign-in";
    }
}
