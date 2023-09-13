package com.example.fileimports.service.implementation;

import com.example.fileimports.dto.FileDetailsResponse;
import com.example.fileimports.model.FileDetails;
import com.example.fileimports.repository.FileDetailsRepository;
import com.example.fileimports.service.FileDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FileDetailsServiceImpl implements FileDetailsService {
    private final FileDetailsRepository fileDetailsRepository;
    @Override
    public List<FileDetailsResponse> getAllUploadedFile() {
        List<FileDetails> fileDetails = fileDetailsRepository.findAll();
        return fileDetails.stream()
                .map(details -> FileDetailsResponse.builder().build())
                .toList();
    }
}
