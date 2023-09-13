package com.example.fileimports.service;

import com.example.fileimports.dto.FileDetailsResponse;

import java.util.List;

public interface FileDetailsService {

    List<FileDetailsResponse> getAllUploadedFile();
}
