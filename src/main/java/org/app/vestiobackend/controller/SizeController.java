package org.app.vestiobackend.controller;

import lombok.RequiredArgsConstructor;
import org.app.vestiobackend.dto.SizeGroupDto;
import org.app.vestiobackend.mapper.SizeGroupMapper;
import org.app.vestiobackend.repository.SizeGroupRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeGroupRepository sizeGroupRepository;
    private final SizeGroupMapper sizeGroupMapper;

    @GetMapping
    @Cacheable("sizes")
    public ResponseEntity<List<SizeGroupDto>> getAllSizes() {
        List<SizeGroupDto> sizes = sizeGroupRepository.findAll().stream()
                .map(sizeGroupMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sizes);
    }
}
