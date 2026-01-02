package com.example.gallery.controller;

import com.example.gallery.entity.Image;
import com.example.gallery.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*") // Allow all origins for simplicity in dev
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    public ResponseEntity<List<Image>> listImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        imageService.uploadImage(file);
        return ResponseEntity.ok("Upload success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok("Delete success");
    }
}
