package com.example.fileimports.service;

import com.example.fileimports.dto.ProductRequest;
import com.example.fileimports.dto.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductResponse> allProducts();
    List<ProductResponse> addProductUsingFile(MultipartFile file) throws IOException;
    String createWordTest() throws IOException;
    public Resource download() throws IOException;
}
