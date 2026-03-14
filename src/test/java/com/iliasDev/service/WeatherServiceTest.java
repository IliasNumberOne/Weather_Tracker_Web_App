package com.iliasDev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliasDev.BaseIntegrationTest;
import com.iliasDev.model.dto.LocationDto;
import com.iliasDev.model.dto.WeatherDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class WeatherServiceTest{
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private WeatherService weatherService;
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        objectMapper = new ObjectMapper();
        weatherService = new WeatherService(httpClient, objectMapper);


        httpResponse = mock(HttpResponse.class);
    }

    @Test
    void testFindLocationsByQuery_success() throws Exception {
        String jsonResponse = "[{\"name\":\"Warsaw\",\"lat\":52.2297,\"lon\":21.0122,\"country\":\"PL\",\"state\":\"Mazowieckie\"}]";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        List<LocationDto> locations = weatherService.findLocationsByQuery("Warsaw");

        assertNotNull(locations);
        assertEquals(1, locations.size());
        LocationDto loc = locations.get(0);
        assertEquals("Warsaw", loc.name());
        assertEquals(new BigDecimal("52.2297"), loc.lat());
        assertEquals(new BigDecimal("21.0122"), loc.lon());
        assertEquals("PL", loc.country());
        assertEquals("Mazowieckie", loc.state());
    }

    @Test
    void testFindLocationsByQuery_apiError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("Internal Server Error");
        when(httpClient.send(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.findLocationsByQuery("Nowhere"));
        assertTrue(ex.getMessage().contains("OpenWeather API error"));
    }

    @Test
    void testGetWeather_success() throws Exception {
        String jsonResponse = "{\"coord\":{\"lon\":21.0122,\"lat\":52.2297},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"main\":{\"temp\":10.5,\"feels_like\":10.0,\"temp_min\":9.0,\"temp_max\":12.0,\"pressure\":1015,\"humidity\":60}}";
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        WeatherDto weather = weatherService.getWeather(BigDecimal.valueOf(52.2297), BigDecimal.valueOf(21.0122));

        assertNotNull(weather);
        assertEquals(10.5, weather.main().temp());
    }

    @Test
    void testGetWeather_ioException() throws Exception {
        when(httpClient.send(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.getWeather(BigDecimal.valueOf(52.2297), BigDecimal.valueOf(21.0122)));
        assertTrue(ex.getMessage().contains("Failed to call OpenWeatherApi"));
    }
}
