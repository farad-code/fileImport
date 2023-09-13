package com.example.fileimports.controller;


import com.example.fileimports.dto.FileDetailsResponse;
import com.example.fileimports.service.FileDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/file-details")
@CrossOrigin("*")
public class FileDetailsController {

    private final FileDetailsService fileDetailsService;


    @GetMapping
    public List<FileDetailsResponse> getAllUploadedFile(){
        return fileDetailsService.getAllUploadedFile();
    }
}
