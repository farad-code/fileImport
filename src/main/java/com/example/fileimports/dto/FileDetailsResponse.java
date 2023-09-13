package com.example.fileimports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDetailsResponse {

    private UUID id;
    private String filename;
    private String fileType;
    private String fileSIze;
    private LocalDateTime createdAt;
}
