package com.iliasDev.repository;

import com.iliasDev.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserRepository {
    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        return sessionFactory.getCurrentSession()
                .createQuery("from User as u where u.login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }
}
