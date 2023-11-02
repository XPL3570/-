package com.confession;

import com.aliyuncs.exceptions.ClientException;
import com.confession.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OOSTest {

    @Resource
    private ImageService imageService;

    @Test
    public void testOOS(){
        long startTime = System.currentTimeMillis();
        imageService.uploadImageOOS();
        long endTime = System.currentTimeMillis();
        System.out.println("获取签名时间:"+(endTime-startTime)+"ms");
    }

    @Test
    public void testOOSWeb(){
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            imageService.uploadImageOOSWeb();
            long endTime = System.currentTimeMillis();
            System.out.println("获取签名时间:"+(endTime-startTime)+"ms");
        }
    }

    @Test
    public void testSBZJ() throws ClientException {
        Long startTime;
        Long endTime;
        for (int i = 1; i <=10 ; i++) {
            startTime = System.currentTimeMillis();
            imageService.alibabaCloudDirectServerSignature();
            endTime = System.currentTimeMillis();
            System.out.println("第"+i+"次，获取签名时间:"+(endTime-startTime)+"ms");
        }
    }


}
