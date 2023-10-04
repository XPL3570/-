package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.mapper.GlobalCarouselImageMapper;
import com.confession.pojo.GlobalCarouselImage;
import com.confession.service.GlobalCarouselImageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
}
