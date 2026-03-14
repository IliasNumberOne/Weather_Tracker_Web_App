package com.iliasDev.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasDev.model.dto.LocationDto;
import com.iliasDev.model.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class WeatherService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String API_KEY;

    public WeatherService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }


    public List<LocationDto> findLocationsByQuery(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = String.format("https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s",
                    encodedQuery,
                    10,
                    API_KEY);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("OpenWeather API error: " + response.body());
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call OpenWeatherApi. Exception: ", e);
        }

    }

    public WeatherDto getWeather(BigDecimal lat, BigDecimal lon)
            throws IOException, InterruptedException {

        try{
            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric",
                    lat,
                    lon,
                    API_KEY
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), WeatherDto.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call OpenWeatherApi", e);
        }
    }
}
