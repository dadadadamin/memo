package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double lat;
    private double lng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id") //  메모 단위로 연결
    private Memo memo;
}