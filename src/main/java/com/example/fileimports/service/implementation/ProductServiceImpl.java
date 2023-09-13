package com.example.fileimports.service.implementation;

import com.example.fileimports.dto.ProductResponse;
import com.example.fileimports.model.FileDetails;
import com.example.fileimports.model.Product;
import com.example.fileimports.repository.FileDetailsRepository;
import com.example.fileimports.repository.ProductRepository;
import com.example.fileimports.service.ProductService;
import com.example.fileimports.utils.FileReaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.fileimports.constant.Constant.FILENAME;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final FileReaderUtils fileReader;
    private final ResourceLoader resourceLoader;
    private final FileDetailsRepository fileDetailsRepository;

    @Override
    public List<ProductResponse> allProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .color(product.getColor())
                        .price(product.getPrice())
                        .brand(product.getBrand())
                        .id(product.getId())
                        .build())
                .toList();
    }

    @Override
    public List<ProductResponse> addProductUsingFile(MultipartFile file) throws IOException {
       List<Product> products = determineFileFormat(file);
       List<Product> savedProducts = productRepository.saveAll(products);
        Map<String,String> fileInfo = fileReader.extractFileDetails(file);
        FileDetails fileDetails = FileDetails.builder()
                .fileSize(fileInfo.get("size"))
                .filename(fileInfo.get("filename"))
                .fileType(fileInfo.get("type"))
                .build();
        fileDetailsRepository.save(fileDetails);
        return savedProducts.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .color(product.getColor())
                        .price(product.getPrice())
                        .brand(product.getBrand())
                        .name(product.getName())
                        .build())
                .toList();
    }

    @Override
    public String createWordTest() throws IOException {
        List<Product> products = productRepository.findAll();
        fileReader.writeToWordFile(products);
        return "Word created";
    }

    @Override
    public Resource download() throws IOException {
        List<Product> products = productRepository.findAll();
        fileReader.writeToWordFile(products);
        return new ClassPathResource(FILENAME+".docx");
    }


    private Resource loadFileAsResource() {
        Resource fileResource = resourceLoader.getResource("classpath:/path/to/files/" + FILENAME);
        if (fileResource.exists()) {
            return fileResource;
        } else {
            // Handle the case where the file does not exist
            throw new RuntimeException("File not found: " + FILENAME);
        }
    }

    private List<Product> determineFileFormat(MultipartFile file) throws IOException {
        String mimeType = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
        List<Product> products = new ArrayList<>();
        switch (mimeType) {
            case "docs", "docx" -> products = fileReader.readWordFile(file);
            case "xls", "xlsx" -> products = fileReader.readExcelFile(file);
            case "csv" -> products = fileReader.readCsvFile(file);
            default -> {
                break;
            }
        }
        return products;
    }
}
