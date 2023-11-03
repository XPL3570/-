package com.confession.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.confession.comm.Result;
import com.confession.config.ALiYunConfig;
import com.confession.dto.ImageUploadKeyDTO;
import com.confession.dto.ImageUploadKeyWebDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.ImageDeleteRecord;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import com.confession.service.ImageDeleteRecordService;
import com.confession.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private ALiYunConfig config;

    @Resource
    private ImageDeleteRecordService imageDeleteRecordService;

    // todo 优化点，一次调用执行了一秒，下面的管理员会代码短不好，，先都使用这个这里使用到了tst，下面的不好说，后面优化
    @Override
    public Result uploadImageOOS() {       // STS接入地址，例如sts.cn-hangzhou.aliyuncs.com。
        String endpoint = config.getEndpoint();
        // 从环境变量中获取步骤1生成的RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = config.getKeyId();
        String accessKeySecret = config.getKeySecret();
        // 从环境变量中获取步骤3生成的RAM角色的RamRoleArn。
        String roleArn = config.getRamRoleArn();
        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
        String roleSessionName = "RamOssTxbbq";
        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket下的src目录上传文件。
        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的src目录。
        // 如果policy为空，则用户将获得该角色下所有权限。
        // 设置临时访问凭证的有效时间为1000秒。 限制15分钟到一小时
        Long durationSeconds = 1000L; //todo,这个过期时间都这么长了，是不是可以放在内存里面不用多次生产了
        ImageUploadKeyDTO keyDTO;
        try {
            // regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
            String regionId = "";
            // 添加endpoint。适用于Java SDK 3.12.0及以上版本。
//            DefaultProfile.addEndpoint(config.getBucketName(), regionId);
            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
            DefaultProfile.addEndpoint("", regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
//            request.setMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
//            request.setPolicy(policy);  //不设置，默认拥有改角色的所有权限
            request.setDurationSeconds(durationSeconds);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            keyDTO = new ImageUploadKeyDTO();
            //  response.getCredentials().getExpiration() //过期时间，到时候看要不要加
            keyDTO.setAccessKeyId(response.getCredentials().getAccessKeyId());

            keyDTO.setSecurityToken(response.getCredentials().getSecurityToken());
            keyDTO.setHost(config.getEndpointNode());  //设置访问节点

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(new Date());

            String key="src/"+format+"/"+this.generateFileName(".jpg"); //优化点 后期优化，这里后缀名不拿了

            keyDTO.setKey(key);

            OSSClient ossClient = new OSSClient(endpoint, response.getCredentials().getAccessKeyId(), response.getCredentials().getAccessKeySecret());
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 2 * 1024 * 1024);
            //根据参数dir计算的policy，如果和前端uploadfile中参数key的相应字段不一致的话是会报错的
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, key);
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes();
            String encodedPolicy = BinaryUtil.toBase64String(binaryData); //base64转码之后的权限标识
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            ossClient.shutdown();//业务完成一定要调用shutdown
            keyDTO.setPolicyBase64Str(encodedPolicy);
            keyDTO.setSignature(postSignature);
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
            throw new WallException("获取上传秘钥失败！", 201);
        }
        if (keyDTO != null) {
            return Result.ok(keyDTO);
        } else {
            return Result.fail();
        }
    }

    @Override
    public Result uploadImageOOSWeb(){
        String endpoint = config.getEndpoint();
        // 从环境变量中获取步骤1生成的RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = config.getKeyId();
        String accessKeySecret = config.getKeySecret();
        // 从环境变量中获取步骤3生成的RAM角色的RamRoleArn。
        String roleArn = config.getRamRoleArn();
        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
        String roleSessionName = "RamOssTxbbq";
        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket下的src目录上传文件。
        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的src目录。
        // 如果policy为空，则用户将获得该角色下所有权限。
        // 设置临时访问凭证的有效时间为300秒。
        Long durationSeconds = 1000L;
        ImageUploadKeyWebDTO keyDTO;
        try {
            // regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
            String regionId = "";
            DefaultProfile.addEndpoint("", regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
//            request.setMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
//            request.setPolicy(policy);  //不设置，默认拥有改角色的所有权限
            request.setDurationSeconds(durationSeconds);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            keyDTO = new ImageUploadKeyWebDTO();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(new Date());

            String key="src/"+format+"/"+this.generateFileName(".jpg");

            OSSClient ossClient = new OSSClient(endpoint, response.getCredentials().getAccessKeyId(), response.getCredentials().getAccessKeySecret());
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 2 * 1024 * 1024);
            //根据参数dir计算的policy，如果和前端uploadfile中参数key的相应字段不一致的话是会报错的
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, key);
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);

            byte[] binaryData = postPolicy.getBytes();
            String encodedPolicy = BinaryUtil.toBase64String(binaryData); //base64转码之后的权限标识
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            ossClient.shutdown();//业务完成一定要调用shutdown
            keyDTO.setDir(key); //直接直接是文件了，不是可访问目录
            keyDTO.setPolicy(encodedPolicy);
            keyDTO.setSignature(postSignature);
            keyDTO.setAccessid(response.getCredentials().getAccessKeyId());
            keyDTO.setExpire(response.getCredentials().getExpiration() );   //  //过期时间，到时候看要不要加
            keyDTO.setHost(config.getEndpointNode());  //设置访问节点
//            keyDTO.setSecurityToken(response.getCredentials().getSecurityToken());
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
            throw new WallException("获取上传秘钥失败！", 201);
        }
        if (keyDTO != null) {
            return Result.ok(keyDTO);
        } else {
            return Result.fail();
        }
    }

    @Override
    public Result alibabaCloudDirectServerSignature() throws ClientException {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        credentialsProvider.getCredentials().getSecurityToken();
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = config.getEndpoint();
        // 填写Bucket名称，例如examplebucket。
        String bucket = config.getBucketName();
        // 填写Host名称，格式为https://bucketname.endpoint。
        String host = config.getEndpointNode();
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM/dd");
        String format = simpleDateFormat.format(new Date());
        String dir="src/"+format+"/"+this.generateFileName(".jpg");
//        String dir = "src/";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 2*1024*1024);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String accessId = credentialsProvider.getCredentials().getAccessKeyId();
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
//            Map<String, String> respMap = new LinkedHashMap<String, String>();
//            respMap.put("accessid", accessId);
//            respMap.put("policy", encodedPolicy);
//            respMap.put("signature", postSignature);
//            respMap.put("dir", dir);
//            respMap.put("host", host);
//            respMap.put("expire", String.valueOf(expireEndTime / 1000));
//            System.out.println(respMap);
            ImageUploadKeyWebDTO keyDTO=new ImageUploadKeyWebDTO();
            keyDTO.setDir(dir); //直接直接是存放文件了，不是可访问目录
            keyDTO.setPolicy(encodedPolicy);
            keyDTO.setSignature(postSignature);
            keyDTO.setAccessid(accessId);
            keyDTO.setExpire(String.valueOf(expireEndTime / 1000) );    //过期时间，到时候看要不要加
            keyDTO.setHost(host);  //设置访问节点
//            System.out.println(keyDTO);
            return Result.ok(keyDTO);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return Result.fail();
    }


    private static final String UPLOAD_PATH = "e:\\表白墙项目图片上传地址\\"; // 设置图片上传路径，这里是windows系统的

    private static final String DOMAIN_NAME_ADDRESS = "http://127.0.0.1:2204/upload/";  //后面可以改成读取配置文件，设置成域名

    @Override
    public Result deleteImage(DeleteImageRequest request,Integer userId) {
        int startIndex = request.getDeleteUrl().indexOf("src/");
        String imageUrl="";
        if (startIndex != -1) {
            imageUrl = request.getDeleteUrl().substring(startIndex);
        } else {
            System.out.println("未找到匹配的字符串");
            throw new WallException("失败",201);
        }
        if (imageUrl.length()!=0){
            ImageDeleteRecord image = new ImageDeleteRecord();
            image.setImagePath(imageUrl);//设置相对路径
            image.setUserId(userId);
            image.setCreateTime(new Date());
            imageDeleteRecordService.save(image);
        }
        return Result.ok();
    }


    public Result deleteImageFeiQi(DeleteImageRequest request) { //删除本地图片，这里弃用
        try {
            // 提取图片的相对路径或文件名
            String imagePath = getImagePathFromUrl(request.getDeleteUrl());

            // 构建图片的完整路径
            String fullPath = UPLOAD_PATH + imagePath;

            File imageFile = new File(fullPath);
            if (imageFile.exists()) { // 判断图片文件是否存在
                if (imageFile.delete()) { // 删除图片文件
                    return Result.ok("删除成功");
                } else {
                    return Result.fail("删除失败");
                }
            } else {
                System.out.println("用户id是：" + JwtInterceptor.getUser().getId() + "删除图片不存在");
                return Result.fail("图片不存在");
            }
        } catch (Exception e) {
            return Result.fail("删除异常：" + e.getMessage());
        }
    }

    private String getImagePathFromUrl(String imageUrl) {
        try {
            // 去掉域名和端口部分，只保留相对路径或文件名
            return imageUrl.replace(DOMAIN_NAME_ADDRESS, "");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl);
        }
    }

    @Override
    public Result upload(MultipartFile file) {
        try {
            // 检查图片大小是否超过2MB
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.build(400, "上传的图片大小超过2MB");
            }
            // 获取图片后缀
            String fileExtension = getFileExtensionZj(file.getOriginalFilename());
            if (fileExtension == null || fileExtension.isEmpty()) {
                fileExtension = "png";
            }

            // 根据日期和随机数生成文件名，并加上文件扩展名
            String fileName = generateFileName("." + fileExtension);

            // 将文件保存到本机
            File destFile = new File(UPLOAD_PATH, fileName);
            file.transferTo(destFile);

            // 返回图片地址
            return Result.ok(DOMAIN_NAME_ADDRESS + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.build(500, "图片上传失败");
        }
    }

    private String getFileExtensionZj(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return null;
    }


    @Override
    public Result uploadImage(UploadImageRequest request) {
        try {
            // 将Base64字符串解码为字节数组
            byte[] imageBytes = Base64Utils.decodeFromString(request.getBase64Image());

            // 检查图片大小是否超过2MB
            if (imageBytes.length > 2 * 1024 * 1024) {
                return Result.build(400, "上传的图片大小超过2MB");
            }

            // 获取图片后缀
            String fileExtension = getImageExtensionFromBase64(request.getBase64Image());
            if (fileExtension == null || fileExtension == "") {
                fileExtension = "png";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
            String folderPath = UPLOAD_PATH + File.separator + dateFormat.format(new Date());
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 根据日期和随机数生成文件名，并加上文件扩展名
            String fileName = generateFileName("." + fileExtension);

            // 构建文件对象
            File file = new File(folderPath, fileName);
            // 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
            }
            // 返回图片地址
            return Result.ok(DOMAIN_NAME_ADDRESS + folder.getName() + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.build(500, "图片上传失败");
        }
    }


    private String generateFileName(String fileExtension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss"); //日，时分秒
        String datePrefix = dateFormat.format(new Date());
        String randomSuffix = generateRandomSuffix(6); // 生成6位随机数或英文字母
        return datePrefix + randomSuffix + fileExtension;
    }

    private String generateRandomSuffix(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int type = random.nextInt(2);
            switch (type) {
                case 0:
                    sb.append((char) (random.nextInt(26) + 'a')); // 生成小写字母
                    break;
                case 1:
                    sb.append((char) (random.nextInt(26) + 'A')); // 生成大写字母
                    break;
            }
        }
        return sb.toString();
    }

    public static String getImageExtensionFromBase64(String base64Image) {
        // 解码Base64图片信息为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // 读取图片的前几个字节
        byte[] headerBytes = new byte[4];
        System.arraycopy(imageBytes, 0, headerBytes, 0, 4);

        // 判断图片格式并获取后缀名
        String extension = "";
        if (headerBytes[0] == (byte) 0xFF && headerBytes[1] == (byte) 0xD8 && headerBytes[2] == (byte) 0xFF) {
            extension = "jpg";
        } else if (headerBytes[0] == (byte) 0x89 && headerBytes[1] == (byte) 0x50 && headerBytes[2] == (byte) 0x4E) {
            extension = "png";
        } else if (headerBytes[0] == (byte) 0x47 && headerBytes[1] == (byte) 0x49 && headerBytes[2] == (byte) 0x46) {
            extension = "gif";
        } else if (headerBytes[0] == (byte) 0x42 && headerBytes[1] == (byte) 0x4D) {
            extension = "bmp";
        }
        return extension;
    }

}
