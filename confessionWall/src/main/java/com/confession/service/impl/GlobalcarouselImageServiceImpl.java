package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.mapper.GlobalCarouselImageMapper;
import com.confession.pojo.GlobalCarouselImage;
import com.confession.request.GlobalCarouselSetRequest;
import com.confession.service.GlobalCarouselImageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xpl
 * @since 2023年10月04日
 */
@Service
public class GlobalcarouselImageServiceImpl extends ServiceImpl<GlobalCarouselImageMapper, GlobalCarouselImage> implements GlobalCarouselImageService {

    @Resource
    private GlobalCarouselImageMapper mapper;

    @Override
    public List<GlobalCarouselImage> getGlobalCarouselImages() {
        LambdaQueryWrapper<GlobalCarouselImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GlobalCarouselImage::getIsDisable,0);
        List<GlobalCarouselImage> list = mapper.selectList(null);
        return list;
    }

    @Override
    public List<GlobalCarouselImage> getAllGlobalCarouselImages() {
        List<GlobalCarouselImage> list = mapper.selectList(null);
        return list;
    }

    @Override
    public void deleteCarouselImage(Integer id) {
        mapper.deleteById(id);
    }

    @Override
    public void addCarouselImage(String addr) {
        GlobalCarouselImage carouselImage = new GlobalCarouselImage().setCarouselImage(addr);
        mapper.insert(carouselImage);
    }

    @Override
    public void closeCarouselImage() {
        LambdaUpdateWrapper<GlobalCarouselImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(GlobalCarouselImage::getIsDisable,false);
        mapper.update(null,wrapper);
    }

    @Override
    public void openCarouselImage() {
        LambdaUpdateWrapper<GlobalCarouselImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(GlobalCarouselImage::getIsDisable,true);
        mapper.update(null,wrapper);
    }

    @Override
    public void setGlobalCarousel(GlobalCarouselSetRequest request) {
        List<String> newImages = request.getCarouselList();
        // Step 1: 从数据库中获取所有的GlobalCarouselImage数据
        List<GlobalCarouselImage> existingImages = mapper.selectList(null);

        // Step 2: 比较数据库中的数据和给定的列表，找出需要添加、和删除的数据
        List<GlobalCarouselImage> imagesToAdd = new ArrayList<>();
        List<GlobalCarouselImage> imagesToDelete = new ArrayList<>();

        for (String newImage : newImages) {
            boolean exists = false;
            for (GlobalCarouselImage existingImage : existingImages) {
                if (newImage.equals(existingImage.getCarouselImage())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                GlobalCarouselImage image = new GlobalCarouselImage();
                image.setCarouselImage(newImage);
                imagesToAdd.add(image);
            }
        }

        for (GlobalCarouselImage existingImage : existingImages) {
            boolean exists = false;
            for (String newImage : newImages) {
                if (existingImage.getCarouselImage().equals(newImage)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                imagesToDelete.add(existingImage);
            }
        }

        // Step 3: 根据比较结果执行相应的操作
        for (GlobalCarouselImage image : imagesToAdd) {
            mapper.insert(image);
        }

        for (GlobalCarouselImage image : imagesToDelete) {
            mapper.deleteById(image.getId());
        }
        //设置是否禁用
        LambdaUpdateWrapper<GlobalCarouselImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(GlobalCarouselImage::getIsDisable,!request.getCarouselIsDisabled()); //前端传递过来的开启是1
        mapper.update(null,wrapper);
    }
}
