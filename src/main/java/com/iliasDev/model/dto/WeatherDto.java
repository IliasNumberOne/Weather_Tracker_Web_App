package com.iliasDev.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDto(
        @JsonProperty("coord") Coord coord,
        @JsonProperty("weather") Weather[] weather,
        @JsonProperty("main") Main main
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Coord(double lon, double lat) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(Long id, String main, String description, String icon) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            int humidity
    ) {}
}