package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.constants.RocketMQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.entity.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-02-07
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    /**
     * 检查数据是否可用
     * @param data  数据
     * @param type  1-用户名  2-手机号
     * @return true -可用   false -不可用
     */
    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        switch (type){
            case 1:
                queryWrapper.lambda().eq(TbUser::getUsername,data);
                break;
            case 2:
                queryWrapper.lambda().eq(TbUser::getPhone,data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
//        查询数据
//        select count(*) from tb_user where   user_name=? / phone=?
        int count = this.count(queryWrapper);
        return count==0;
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

//    短信验证码的前缀
    private String PRE_Fix = "ly:user:verify:phone:";
    /**
     * 发送短信验证码
     * 验证码生成后需要存放在redis，并设置有效期
     * 验证码后面的业务需要验证
     * @param phone
     * @return
     */
    @Override
    public void sendCode(String phone) {
        if(!RegexUtils.isPhone(phone)){
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
//        随机验证码
        String code = RandomStringUtils.randomNumeric(6);
//        redis的key
        String redisKey = PRE_Fix + phone;
//        存放redis
        redisTemplate.opsForValue().set(redisKey,code,5, TimeUnit.MINUTES);
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
//        发送RocketMQ的消息，内容{phone,code}
        rocketMQTemplate.convertAndSend(SMS_TOPIC_NAME +":"+ VERIFY_CODE_TAGS
                ,JsonUtils.toString(msg));
    }
}
