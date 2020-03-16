package com.leyou.search.repository;

import com.leyou.search.po.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//继承ElasticsearchRepository 包含了 简单的CRUD方法
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
