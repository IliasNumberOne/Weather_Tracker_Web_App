<div style="display: flex; align-items: flex-end;">
  <h1>Weather Tracker Web App</h1>
  <img src="src/main/webapp/images/weather-app.png" alt="icon" width="70" height="70" style="margin-left: 10px;">
</div>

Weather Tracker is a Spring MVC web application (without Spring Boot) that enables users to register, search for global locations, and maintain a personal collection of places with current weather data. The app uses custom session-based authentication, integrates with the OpenWeatherMap API, and provides a responsive Bootstrap user interface.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Security](#security)
- [API Integration](#api-integration)
- [Run with Docker](#-run-with-docker)
- [Local Setup](#local-setup)
- [Running the App](#running-the-app)
- [Testing](#testing)

## Features
- **User Registration & Authentication:** Users can register new accounts and log in. Passwords are securely hashed with BCrypt.
- **Session Management:** Custom session-based authentication (no Spring Security) using HTTP cookies and server-side sessions.
- **Location Search:** Look up locations worldwide by name
- **Saved Locations:** Add and remove locations to a personal collection.
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

### Table: Users
- `id` – Primary key (auto-increment)
- `login` – Unique user login/email
- `password` – BCrypt hashed password

---

### Table: Locations
- `id` – Primary key (auto-increment)
- `name` – Location (city) name
- `latitude` – Geographic latitude
- `longitude` – Geographic longitude
- `country` – Country code
- `state` – State/region (optional)

**Constraint:**
- Unique combination of `(state, country, name)` to prevent duplicate locations

---

### Table: User_Locations
- `user_id` – Foreign key → `users.id`
- `location_id` – Foreign key → `locations.id`

**Details:**
- Composite primary key `(user_id, location_id)`
- Many-to-many relationship between users and locations
- Cascade delete enabled

---

### Table: Sessions
- `id` – UUID primary key
- `user_id` – Foreign key → `users.id`
- `expires_at` – Session expiration timestamp

## Security
- **Authentication:** Custom session-based auth with HTTP cookies. Sessions expire per the `expires_at` field in the database (no Spring Security).
- **Password Hashing:** User passwords are hashed using BCrypt.
- **SQL Injection:** Using Hibernate (JPA) prevents SQL injection vulnerabilities.

## API Integration
- **OpenWeatherMap API:** Used for fetching current weather data for locations. A valid OpenWeatherMap API key must be configured.
- **HTTP Client:** Java's built-in HTTP client (e.g., `HttpClient`) is used to communicate with the API.

## 🐳 Run with Docker

Start the application using Docker:

To build and start containers, run: `docker-compose up --build`

After startup, the application will be available at http://localhost:8080


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
   Edit `src/main/resources/application.properties` or set environment variables for the database connection and API key.
3. **Run Flyway migrations:**  
   `mvn flyway:migrate`  
   This initializes the database schema.
4. **Build the project:**  
   `mvn clean package`
5. **Deploy the application:**  
   - Deploy the generated WAR file to a Tomcat server
   The app defaults to port 8080.

## Running the App
- **Build:** `mvn clean package`  
- **Run:** Start the server/Tomcat. Access the app at `http://localhost:8080`.

## Testing
The project includes unit and integration tests using JUnit 5:
- **Configuration Tests:** Verify that `Config` and `HibernateConfig` load correctly.
- **Service Tests:** `AuthService`, `SessionService`, and `WeatherService` have tests (external API calls are mocked).
