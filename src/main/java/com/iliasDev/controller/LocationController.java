package com.iliasDev.controller;

import com.iliasDev.model.dto.LocationDto;
import com.iliasDev.model.entity.User;
import com.iliasDev.service.LocationService;
import com.iliasDev.service.SessionService;
import com.iliasDev.service.WeatherService;
import com.iliasDev.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Validated
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;
    private final SessionService sessionService;
    private final WeatherService weatherService;
    private final CookieUtil cookieUtil;


    public LocationController(LocationService locationService, SessionService sessionService, WeatherService weatherService, CookieUtil cookieUtil) {
        this.locationService = locationService;
        this.sessionService = sessionService;
        this.weatherService = weatherService;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/search")
    public String search(@RequestParam("searchQuery") @NotBlank String searchQuery,
                         Model model,
                         HttpServletRequest request) {
        UUID sessionID = cookieUtil.getSessionId(request);
        if (sessionID == null) {
            return "redirect:/auth/sign-in";
        }
        Long userId = sessionService.getValidSession(sessionID)
                .map(s -> s.getUserId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        User user = locationService.getUserWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LocationDto> locations = weatherService.findLocationsByQuery(searchQuery);

        model.addAttribute("user", user);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("foundLocations", locations);

        return "search-results";
    }


    @PostMapping
    public String add(@ModelAttribute LocationDto locationDto,
                      HttpServletRequest request) {

        UUID sessionId = cookieUtil.getSessionId(request);

        if (sessionId == null) {
            return "redirect:/auth/sign-in";
        }

        Long userId = sessionService.getValidSession(sessionId)
                .map(s -> s.getUserId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        locationService.addLocation(
                userId,
                locationDto.name(),
                locationDto.lat(),
                locationDto.lon()
        );

        return "redirect:/home";
    }
}
