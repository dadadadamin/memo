package com.example.demo.dto.map;

import com.example.demo.model.MapPlace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapsResponse {
    private List<MapPlaceDto> places;
}