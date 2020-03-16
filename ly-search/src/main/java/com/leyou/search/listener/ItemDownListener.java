package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.*;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

/**
 * 商品上架 的消息监听器
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = ITEM_SEARCH_DOWN_CONSUMER
        ,topic = ITEM_TOPIC_NAME
        ,selectorExpression = ITEM_DOWN_TAGS)
public class ItemDownListener implements RocketMQListener<Long> {

    @Autowired
    private SearchService searchService;
    /**
     * 下架，删除索引库
     * @param spuId
     */
    @Override
    public void onMessage(Long spuId) {
        log.info("接收到【商品下架】消息，spuId={}",spuId);
        searchService.deleteIndex(spuId);

    }
}
