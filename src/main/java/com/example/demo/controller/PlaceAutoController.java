package com.example.demo.controller;

import com.example.demo.dto.PlaceAutoResponse;
import com.example.demo.service.PlaceAutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceAutoController {

    private final PlaceAutoService placeAutoService;

    @GetMapping("/autocomplete")
    public PlaceAutoResponse autocomplete(@RequestParam String input) {
        return placeAutoService.getAutocomplete(input);
    }
}