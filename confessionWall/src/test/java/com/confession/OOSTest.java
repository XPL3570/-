package com.confession;

import com.confession.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OOSTest {

    @Resource
    private ImageService imageService;

    @Test
    public void test01(){
        long startTime = System.currentTimeMillis();
        imageService.uploadImageOOS();
        long endTime = System.currentTimeMillis();
        System.out.println("获取签名时间:"+(endTime-startTime)+"ms");
    }

    @Test
    public void testOOSWeb() throws Exception{
        long startTime = System.currentTimeMillis();
        imageService.uploadImageOOSWeb();
        long endTime = System.currentTimeMillis();
        System.out.println("获取签名时间:"+(endTime-startTime)+"ms");
    }


}
