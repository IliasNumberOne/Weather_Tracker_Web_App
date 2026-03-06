package com.iliasDev.repository;

import com.iliasDev.model.entity.SessionEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepository {
    private final SessionFactory sessionFactory;

    public SessionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionEntity save(SessionEntity sessionEntity) {
        sessionFactory.getCurrentSession().persist(sessionEntity);
        return sessionEntity;
    }

    public Optional<SessionEntity> findById(UUID id) {
        return Optional.ofNullable(
                sessionFactory.getCurrentSession().get(SessionEntity.class, id)
        );
    }

    public void delete(SessionEntity sessionEntity) {
        sessionFactory.getCurrentSession().remove(sessionEntity);
    }

    public void deleteSessionsByUserId(Long userId) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM SessionEntity s where s.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
