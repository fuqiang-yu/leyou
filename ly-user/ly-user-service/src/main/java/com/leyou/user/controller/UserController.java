package com.leyou.user.controller;

import com.leyou.user.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private TbUserService userService;
    /**
     * 检查数据是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable(name = "data")String data,
                                      @PathVariable(name = "type")Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam(name = "phone")String phone){
        userService.sendCode(phone);
        return ResponseEntity.noContent().build();
    }
}
