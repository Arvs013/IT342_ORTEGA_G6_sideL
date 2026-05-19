package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final Path UPLOAD_DIR = Paths.get("uploads", "gigs");

    @PostMapping("/images")
    public ResponseEntity<?> uploadImages(@RequestParam("images") MultipartFile[] images) throws IOException {
        if (images.length > 5) {
            return ResponseEntity.badRequest().body("You can only upload up to 5 images.");
        }

        Files.createDirectories(UPLOAD_DIR);
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                continue;
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed.");
            }

            String originalName = image.getOriginalFilename() == null ? "image" : image.getOriginalFilename();
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalName.substring(dotIndex);
            }

            String filename = UUID.randomUUID() + extension;
            Path target = UPLOAD_DIR.resolve(filename);
            image.transferTo(target);
            imagePaths.add("/uploads/gigs/" + filename);
        }

        return ResponseEntity.ok(imagePaths);
    }
}
