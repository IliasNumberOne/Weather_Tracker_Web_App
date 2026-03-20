package com.iliasDev.service;

import com.iliasDev.exception.LocationAlreadyAddedException;
import com.iliasDev.model.dto.LocationDto;
import com.iliasDev.model.entity.Location;
import com.iliasDev.model.entity.User;
import com.iliasDev.repository.LocationRepository;
import com.iliasDev.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class LocationService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SessionFactory sessionFactory;

    public LocationService(UserRepository userRepository, LocationRepository locationRepository, SessionFactory sessionFactory) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithLocations(Long userId) {
        return userRepository.findByIdWithLocations(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> getLocationSavedMap(User user, List<LocationDto> locations) {
        Set<Location> userLocations = locationRepository.findByUser(user);
        Map<String, Boolean> map = new HashMap<>();

        for (LocationDto locDto : locations) {
            String stateSafe = locDto.state() != null ? locDto.state() : "";
            boolean exists = userLocations.stream()
                    .anyMatch(l ->
                            l.getName().equals(locDto.name()) &&
                                    l.getCountry().equals(locDto.country()) &&
                                    (l.getState() != null ? l.getState() : "").equals(stateSafe)
                    );

            String key = locDto.name() + "-" + locDto.country() + "-" + stateSafe;
            map.put(key, exists);
        }

        return map;
    }


    @Transactional
    public void addLocation(Long userId, String name, BigDecimal lat, BigDecimal lon, String country, String state) {
        User user = userRepository.findByIdWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (locationRepository.existsByUserAndLocation(userId, name, country, state)) {
            throw new LocationAlreadyAddedException("The location " + name + " has already been added");
        }

        Location location = new Location();
        location.setName(name);
        location.setLongitude(lon);
        location.setLatitude(lat);
        location.setCountry(country);
        location.setState(state);


        locationRepository.save(location);


        user.getLocations().add(location);
    }

    @Transactional
    public void removeLocationFromUser(Long userId, Long locationId) {
        User user = userRepository.findByIdWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        user.getLocations().remove(location);
        sessionFactory.getCurrentSession().merge(user);
    }

    @Transactional(readOnly = true)
    public Set<Location> getLocationsOfUser(Long userId) {
        User user = userRepository.findByIdWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getLocations();
    }
}
