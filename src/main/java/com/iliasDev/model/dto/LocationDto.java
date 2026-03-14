package com.iliasDev.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationDto(
        String name,
        BigDecimal lat,
        BigDecimal lon,
        String country,
        String state
) { }
