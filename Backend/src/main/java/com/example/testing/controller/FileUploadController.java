package com.example.testing.controller;

import com.example.testing.dto.DistanceResult;
import com.example.testing.services.ExcelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final ExcelService excelService;

    public FileUploadController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public List<DistanceResult> uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return excelService.processExcel(file);
    }


    @GetMapping("/test")
    public String test() {
        return "Excel upload API is working";
    }
}
