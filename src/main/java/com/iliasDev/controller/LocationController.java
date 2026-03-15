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
import java.util.Map;
import java.util.UUID;

@Controller
@Validated
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;
    private final WeatherService weatherService;


    public LocationController(LocationService locationService, WeatherService weatherService) {
        this.locationService = locationService;
        this.weatherService = weatherService;
    }

    @GetMapping("/search")
    public String search(@RequestParam("searchQuery") @NotBlank String searchQuery,
                         Model model,
                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        User user = locationService.getUserWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LocationDto> locations = weatherService.findLocationsByQuery(searchQuery);
        Map<String, Boolean> locationSaved = locationService.getLocationSavedMap(user, locations);


        model.addAttribute("user", user);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("foundLocations", locations);
        model.addAttribute("locationSaved", locationSaved);

        return "search-results";
    }

    @PostMapping
    public String add(@ModelAttribute LocationDto locationDto,
                      HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        locationService.addLocation(
                userId,
                locationDto.name(),
                locationDto.lat(),
                locationDto.lon()
        );

        return "redirect:/home";
    }
}
