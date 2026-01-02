package com.example.gallery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gallery.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService extends IService<Image> {
    void uploadImage(MultipartFile file);

    List<Image> getAllImages();

    void deleteImage(Long id);
}
