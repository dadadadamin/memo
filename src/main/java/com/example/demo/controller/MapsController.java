package com.example.demo.controller;

import com.example.demo.dto.map.MapsRequest;
import com.example.demo.dto.map.MapsResponse;
import com.example.demo.service.MapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapsController {

    private final MapsService mapsService;

    @PostMapping
    public ResponseEntity<MapsResponse> analyzeMemo(@RequestBody MapsRequest request) {
        MapsResponse response = mapsService.analyzeMemo(request);
        return ResponseEntity.ok(response);
    }
}