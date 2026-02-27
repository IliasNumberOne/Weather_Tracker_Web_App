-- USERS
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- LOCATIONS
CREATE TABLE locations
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255)  NOT NULL,
    latitude  DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,

    UNIQUE (latitude, longitude)
);

-- USER_LOCATIONS
CREATE TABLE user_locations
(
    user_id INT NOT NULL,
    location_id INT NOT NULL,
    PRIMARY KEY (user_id, location_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

-- SESSIONS
CREATE TABLE sessions
(
    id         UUID PRIMARY KEY,
    user_id    INT       NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_sessions_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

