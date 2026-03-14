package com.iliasDev.repository;

import com.iliasDev.model.entity.Location;
import com.iliasDev.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class LocationRepository {
    private final SessionFactory sessionFactory;

    public LocationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Location location) {
        sessionFactory.getCurrentSession().persist(location);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Location.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByCoordinates(BigDecimal lat, BigDecimal lon) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "from Location l where l.latitude = :lat and l.longitude = :lon",
                        Location.class
                )
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByName(String name) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Location l where l.name = :name", Location.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public Set<Location> findByUser(User user) {
        User managedUser = sessionFactory.getCurrentSession().get(User.class, user.getId());
        return managedUser.getLocations();
    }

    @Transactional
    public void delete(Location location) {
        sessionFactory.getCurrentSession().remove(location);
    }
}
