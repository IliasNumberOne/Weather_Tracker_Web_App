# Weather Tracker Web App

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](#)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow)](#)

**Executive Summary:** Weather Tracker is a Spring MVC web application (without Spring Boot) that enables users to register, search for global locations, and maintain a personal collection of places with current weather data. The app uses custom session-based authentication, integrates with the OpenWeatherMap API, and provides a responsive Bootstrap user interface.

This README describes the project structure, setup instructions, and usage details.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Security](#security)
- [API Integration](#api-integration)
- [Deployment (Docker)](#deployment-docker)
- [Local Setup](#local-setup)
- [Running the App](#running-the-app)
- [Testing](#testing)
- [Application Pages](#application-pages)
- [Screenshots](#screenshots)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features
- **User Registration & Authentication:** Users can register new accounts and log in. Passwords are securely hashed with BCrypt.
- **Session Management:** Custom session-based authentication (no Spring Security) using HTTP cookies and server-side sessions.
- **Location Search:** Look up locations worldwide by name (with optional country/state qualifiers).
- **Saved Locations:** Add and remove locations to a personal collection. Duplicate (state, country, name) entries are prevented by the database schema.
- **Real-Time Weather:** View current weather data for all saved locations via the OpenWeatherMap API.
- **Responsive UI:** Clean, mobile-friendly interface built with Bootstrap 5 and Thymeleaf.

## Tech Stack
- **Java 17** – programming language
- **Spring MVC** – web framework (manual configuration, no Spring Boot)
- **Hibernate (JPA)** – ORM for database access
- **Maven 3.6+** – build and dependency management
- **PostgreSQL** – database
- **Flyway** – database migration management
- **Thymeleaf** – server-side HTML templates
- **Bootstrap 5** – frontend UI framework
- **Java HTTP Client** – for calling the OpenWeatherMap API
- **JUnit 5** – testing framework

## Architecture
The application follows a traditional Model-View-Controller (MVC) design:

- **Controllers:** Handle HTTP requests (e.g., `AuthController`, `LocationController`).
- **Services:** Business logic (e.g., `AuthService`, `SessionService`, `WeatherService`).
- **Repositories (DAOs):** Data access layer with Hibernate/JPA entities (`User`, `Location`, `Session`).
- **Views:** Thymeleaf templates for HTML pages.

Configuration is done via Java classes (`Config`, `HibernateConfig`). Flyway handles database migrations on startup. The layered design (controllers → services → repositories) makes the app maintainable and testable.

## Database Schema

The application uses a PostgreSQL database with the following tables:

    -- USERS
    CREATE TABLE users
    (
        id       BIGSERIAL PRIMARY KEY,
        login    VARCHAR(255) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL
    );

    -- LOCATIONS
    CREATE TABLE locations
    (
        id        BIGSERIAL PRIMARY KEY,
        name      VARCHAR(255)  NOT NULL,
        latitude  DECIMAL(9, 6) NOT NULL,
        longitude DECIMAL(9, 6) NOT NULL,
        country   VARCHAR(10) NOT NULL,
        state     VARCHAR(255),

        UNIQUE (state, country, name)
    );

    -- USER_LOCATIONS
    CREATE TABLE user_locations
    (
        user_id BIGINT NOT NULL,
        location_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, location_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
    );

    -- SESSIONS
    CREATE TABLE sessions
    (
        id         UUID PRIMARY KEY,
        user_id    BIGINT       NOT NULL,
        expires_at TIMESTAMP NOT NULL,

        CONSTRAINT fk_sessions_user
            FOREIGN KEY (user_id)
                REFERENCES users (id)
                ON DELETE CASCADE
    );

## Security
- **Authentication:** Custom session-based auth with HTTP cookies. Sessions expire per the `expires_at` field in the database (no Spring Security).
- **Password Hashing:** User passwords are hashed using BCrypt.
- **SQL Injection:** Using Hibernate (JPA) prevents SQL injection vulnerabilities.
- **XSS Protection:** Thymeleaf templates auto-escape output to guard against cross-site scripting.

## API Integration
- **OpenWeatherMap API:** Used for fetching current weather data for locations. A valid OpenWeatherMap API key must be configured.
- **HTTP Client:** Java's built-in HTTP client (e.g., `HttpClient`) is used to communicate with the API.

## Deployment (Docker)
This project includes Docker support for easy deployment:

- **PostgreSQL:** The database runs in a Docker container (official `postgres` image).
- **Application:** The Spring MVC app runs in a Docker container (built via Dockerfile).

Example `docker-compose.yml` (service definitions):

    version: '3.8'
    services:
      db:
        image: postgres:13
        environment:
          - POSTGRES_DB=${POSTGRES_DB}
          - POSTGRES_USER=${POSTGRES_USER}
          - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
        volumes:
          - db-data:/var/lib/postgresql/data

      app:
        build: .
        environment:
          - DB_HOST=db
          - DB_PORT=5432
          - DB_NAME=${POSTGRES_DB}
          - DB_USER=${POSTGRES_USER}
          - DB_PASSWORD=${POSTGRES_PASSWORD}
          - OPENWEATHER_API_KEY=${OPENWEATHER_API_KEY}
        ports:
          - "${APP_PORT}:8080"
        depends_on:
          - db

    volumes:
      db-data:

You can configure environment variables in a `.env` file. Example `.env`:

    POSTGRES_DB=weathertracker
    POSTGRES_USER=weatheruser
    POSTGRES_PASSWORD=weatherpass
    APP_PORT=8080
    OPENWEATHER_API_KEY=your_api_key_here

To build and start containers, run: `docker-compose up --build`

After startup, the application will be available at [http://localhost:8080](http://localhost:8080).

## Local Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL database
- OpenWeatherMap API key

### Steps
1. **Clone the repository:**  
   `git clone https://github.com/IliasNumberOne/Weather_Tracker_Web_App.git`  
   `cd Weather_Tracker_Web_App`
2. **Configure the application:**  
   Edit `src/main/resources/application.properties` or set environment variables for the database connection and API key. For example:  
   - `spring.datasource.url` (or use `DB_HOST`, `DB_PORT`, etc.)  
   - `spring.datasource.username` and `spring.datasource.password`  
   - `openweathermap.apikey` (or `OPENWEATHER_API_KEY`)
3. **Run Flyway migrations:**  
   `mvn flyway:migrate`  
   This initializes the database schema.
4. **Build the project:**  
   `mvn clean package`
5. **Deploy the application:**  
   - Deploy the generated WAR file to a Tomcat server, **or**  
   - Run with Maven/Tomcat plugin (e.g., `mvn tomcat7:run`), if configured.  
   The app defaults to port 8080.

## Running the App
- **Build:** `mvn clean package`  
- **Run:** Start the server/Tomcat. Access the app at `http://localhost:8080`.  
- **With Docker:** If using Docker Compose, run `docker-compose up --build` (see above). Then visit `http://localhost:8080`.

## Testing
The project includes unit and integration tests using JUnit 5:
- **Configuration Tests:** Verify that `Config` and `HibernateConfig` load correctly.
- **Service Tests:** `AuthService`, `SessionService`, and `WeatherService` have tests (external API calls are mocked).
- **Run Tests:**  
  `mvn test`

## Application Pages
- **Home Page:** Displays the user's saved locations and current weather data.
- **Search Results:** Shows matching locations for a search query, with an option to add them to the collection.
- **Login / Register:** Forms for user authentication (`/login`, `/register`).
- **Error Pages:** Friendly error messages for 404, 500, etc.

## Screenshots
*(Replace with actual screenshots. Save images in a `screenshots/` folder and update paths.)*

![Home Page](screenshots/home.png)  
![Search Page](screenshots/search.png)

## Troubleshooting
- **Database Connection:** If the app cannot connect to PostgreSQL, verify that the database is running and the connection settings (URL, username, password) are correct.
- **Port Conflicts:** The app listens on port 8080 by default. Change `APP_PORT` or adjust your server if needed.
- **API Key Issues:** If weather data isn't loading, ensure that the `OPENWEATHER_API_KEY` is set properly and has not exceeded rate limits.
- **Session Expiration:** Sessions expire based on the `expires_at` timestamp. If users are logged out unexpectedly, check the system time and session timeout configuration.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request with your changes. Ensure that new features or fixes include corresponding tests. Feel free to file issues for bugs or enhancements.

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Contact
Project repository: [IliasNumberOne/Weather_Tracker_Web_App](https://github.com/IliasNumberOne/Weather_Tracker_Web_App)

For questions or feedback, open an issue on GitHub or contact the maintainer.
