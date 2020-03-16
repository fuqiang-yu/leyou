package com.leyou.user.service;

import com.leyou.user.entity.TbUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HM
 * @since 2020-02-07
 */
public interface TbUserService extends IService<TbUser> {

    /**
     * 检查数据是否可用
     * @param data
     * @param type
     * @return
     */
    Boolean checkData(String data, Integer type);
    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    void sendCode(String phone);
}
