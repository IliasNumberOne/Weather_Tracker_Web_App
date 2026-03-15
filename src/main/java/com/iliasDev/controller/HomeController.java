package com.iliasDev.controller;

import com.iliasDev.model.dto.WeatherDto;
import com.iliasDev.model.entity.Location;
import com.iliasDev.model.entity.SessionEntity;
import com.iliasDev.model.entity.User;
import com.iliasDev.service.LocationService;
import com.iliasDev.service.SessionService;
import com.iliasDev.service.WeatherService;
import com.iliasDev.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final LocationService locationService;
    private final WeatherService weatherService;

    public HomeController(LocationService locationService, WeatherService weatherService) {
        this.locationService = locationService;
        this.weatherService = weatherService;
    }

    @GetMapping
    public String getHomePage(Model model, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        User user = locationService.getUserWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Location> locations = user.getLocations();
        Map<Location, WeatherDto> locationWeatherDtoMap = new HashMap<>();

        locations.forEach(loc -> {
            try {
                WeatherDto weather = weatherService.getWeather(loc.getLatitude(), loc.getLongitude());
                locationWeatherDtoMap.put(loc, weather);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        model.addAttribute("user", user);
        model.addAttribute("weatherMap", locationWeatherDtoMap);
        return "home";
    }

    @PostMapping("/{locationId}/delete")
    public String deleteLocation(@PathVariable("locationId") Long locationId,
                                 HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        locationService.removeLocationFromUser(userId, locationId);
        return "redirect:/home";
    }

}
