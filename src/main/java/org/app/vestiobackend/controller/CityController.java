package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.CityDto;
import org.app.vestiobackend.mapper.CityMapper;
import org.app.vestiobackend.repository.CityRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @GetMapping
    @Cacheable("cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        List<CityDto> cities = cityRepository.findAll().stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cities);
    }
}
