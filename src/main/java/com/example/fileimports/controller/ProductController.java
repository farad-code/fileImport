package com.example.fileimports.controller;



import com.example.fileimports.dto.ProductResponse;
import com.example.fileimports.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.fileimports.constant.Constant.FILENAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ProductResponse> readFile(@RequestPart("file")MultipartFile file) throws IOException {
        return productService.addProductUsingFile(file);
    }

    @GetMapping
    public List<ProductResponse> products(){
        return productService.allProducts();
    }

    @GetMapping("word")
    public String word() throws IOException {
        return productService.createWordTest();
    }


    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        Resource resource = productService.download();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", FILENAME);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
