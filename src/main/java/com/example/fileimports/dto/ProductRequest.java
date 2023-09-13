package com.example.fileimports.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductRequest {

    private String name;
    private String brand;
    private String color;
    private double price;
}
