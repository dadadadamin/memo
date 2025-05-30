package com.example.demo.dto.map;

import com.example.demo.model.MapPlace;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapPlaceDto {
    private String name;
    private double lat;
    private double lng;


    public MapPlaceDto(MapPlace entity) {
        this.name = entity.getName();
        this.lat = entity.getLat();
        this.lng = entity.getLng();
    }
}