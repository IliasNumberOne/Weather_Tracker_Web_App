package com.iliasDev.service;

import com.iliasDev.model.entity.Location;
import com.iliasDev.model.entity.User;
import com.iliasDev.repository.LocationRepository;
import com.iliasDev.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

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

    @Transactional
    public void addLocation(Long userId, String name, BigDecimal lat, BigDecimal lon) {

        User user = userRepository.findByIdWithLocations(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Location> existingLocation =
                locationRepository.findByCoordinates(lat, lon);

        Location location;

        if (existingLocation.isPresent()) {
            location = existingLocation.get();
        } else {
            location = new Location();
            location.setName(name);
            location.setLatitude(lat);
            location.setLongitude(lon);

            locationRepository.save(location);
        }

        boolean alreadyAdded = user.getLocations().stream()
                .anyMatch(loc -> loc.getLatitude().equals(lat) && loc.getLongitude().equals(lon));

        if (!alreadyAdded) {
            user.getLocations().add(location);
        }
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
