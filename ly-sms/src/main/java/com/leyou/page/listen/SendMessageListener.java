package com.leyou.page.listen;

import com.leyou.common.utils.JsonUtils;
import com.leyou.page.config.SmsProperties;
import com.leyou.page.utils.SmsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.SMS_VERIFY_CODE_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.VERIFY_CODE_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SMS_TOPIC_NAME;

/**
 * 监听验证码短信消息
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = SMS_VERIFY_CODE_CONSUMER
        ,topic = SMS_TOPIC_NAME
        ,selectorExpression = VERIFY_CODE_TAGS)
public class SendMessageListener implements RocketMQListener<String> {

    @Autowired
    private SmsHelper smsHelper;
    @Autowired
    private SmsProperties prop;
    /**
     * 发送短信
     *
     * @param message  {phone:13784949,code:123456}
     */
    @Override
    public void onMessage(String message) {
        Map<String, String> msgMap = JsonUtils.toMap(message,String.class,String.class);
        String phone = msgMap.remove("phone");
        //调用发短信的接口
        smsHelper.sendMessage(phone
                ,prop.getSignName()
                ,prop.getVerifyCodeTemplate()
                ,JsonUtils.toString(msgMap));
    }
}
