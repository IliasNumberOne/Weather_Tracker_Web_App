package com.iliasDev.repository;

import com.iliasDev.model.entity.Location;
import com.iliasDev.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public boolean existsByUserAndLocation(Long userId, String name, String country, String state) {
        List<Location> locations = sessionFactory.getCurrentSession()
                .createQuery(
                        "select l from Location l join l.users u " +
                                "where u.id = :userId " +
                                "and l.name = :name " +
                                "and l.country = :country " +
                                "and (l.state = :state or (l.state is null and :state is null))",
                        Location.class
                )
                .setParameter("userId", userId)
                .setParameter("name", name)
                .setParameter("country", country)
                .setParameter("state", state)
                .list();

        return !locations.isEmpty();
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
