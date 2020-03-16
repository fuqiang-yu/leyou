package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.ITEM_SEARCH_UP_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * 商品上架 的消息监听器
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = ITEM_SEARCH_UP_CONSUMER
        ,topic = ITEM_TOPIC_NAME
        ,selectorExpression = ITEM_UP_TAGS)
public class ItemUpListener implements RocketMQListener<Long> {

    @Autowired
    private SearchService searchService;
    /**
     * 上架，创建索引库内容
     * @param spuId
     */
    @Override
    public void onMessage(Long spuId) {
        log.info("接收到【商品上架】消息，spuId={}",spuId);
        searchService.createIndex(spuId);

    }
}
