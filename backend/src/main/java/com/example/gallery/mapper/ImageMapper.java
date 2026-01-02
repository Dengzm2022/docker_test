package com.example.gallery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gallery.entity.Image;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper extends BaseMapper<Image> {
}
