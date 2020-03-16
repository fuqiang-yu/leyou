package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {
    @Autowired
    private UploadService uploadService;
    /**
     * 本地上传
     */
    @PostMapping("/image")
    public ResponseEntity<String>image(MultipartFile file){
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
    /**
     * 获取阿里云的签名
     * @return
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String, Object>> getSignature(){
        return ResponseEntity.ok(uploadService.getSignature());
    }
}
