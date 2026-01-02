package com.example.gallery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gallery.entity.Image;
import com.example.gallery.mapper.ImageMapper;
import com.example.gallery.service.ImageService;
import com.example.gallery.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    private final MinioUtils minioUtils;

    @Override
    public void uploadImage(MultipartFile file) {
        String objectName = minioUtils.uploadFile(file);
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setUrl(objectName); // Initial URL stored is the object name
        image.setCreateTime(LocalDateTime.now());
        this.save(image);
    }

    @Override
    public List<Image> getAllImages() {
        List<Image> list = this.list();
        list.forEach(item -> {
            // Generate presigned URL for frontend access
            String presignedUrl = minioUtils.getFileUrl(item.getUrl());
            item.setUrl(presignedUrl);
        });
        return list;
    }

    @Override
    public void deleteImage(Long id) {
        Image image = this.getById(id);
        if (image != null) {
            minioUtils.removeFile(image.getUrl()); // Remove from Minio using object name
            this.removeById(id);
        }
    }
}
