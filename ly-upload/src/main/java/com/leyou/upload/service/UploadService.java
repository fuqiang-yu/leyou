package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.upload.config.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UploadService {
    //支持的文件类型
    private static final List<String> suffixes = Arrays.asList(
            "image/png","image/jpeg","image/kmp"
    );

    /**
     * 本地上传图片
     * @param file
     * @return 图片的网络地址
     */
    public String  uploadImage(MultipartFile file) {
        //获取图片类型
        String contentType=file.getContentType();
        //验证图片的类型是否被支持
        if (!suffixes.contains(contentType)){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //验证是不是图片
        try {
            BufferedImage bufferedImage= ImageIO.read(file.getInputStream());
            if (bufferedImage==null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //获取源文件名字
        String originalFilename = file.getOriginalFilename();
        //获取文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //声明一个新的文件名字
        String newName = UUID.randomUUID().toString() + suffix;
        //上传到nginx的html目录
        String filePath="D:\\Develop\\nginx\\nginx-1.13.12\\html";
        //判断文件目录是否存在
        File dir=new File(filePath);
        if (!dir.exists()){
            //目录不存在，创建目录
            dir.mkdir();
        }
        //把文件写入磁盘
        try {
            file.transferTo(new File(dir,newName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        String url= "http://image.leyou.com/"+newName;
        return url;
    }
    @Autowired
    private OSSProperties prop;
    /**
     * 阿里云sdk的对象
     */
    @Autowired
    private OSS client;
    /**
     * 使用阿里云SDK的代码，生成签名
     * @return
     */
    public Map<String, Object> getSignature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
