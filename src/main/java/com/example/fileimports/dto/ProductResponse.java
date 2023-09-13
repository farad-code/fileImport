package com.example.fileimports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String brand;
    private String color;
    private double price;
}
