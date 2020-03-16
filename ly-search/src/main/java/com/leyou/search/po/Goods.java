package com.leyou.search.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * 一个goods对象 与es中的一条文档对应
 */
@Data
//表示当前类和文档的对应关系
@Document(indexName = "goods",type = "docs")
public class Goods {
    //spuid
    @Id //当前属性 是唯一标识id
    @Field(type = FieldType.Keyword)
    private Long id;
    //促销信息 Keyword - 不分词，
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;
    //- 用来显示  skuid,title,price,image
    @Field(type = FieldType.Keyword,index = false)
    private String skus ;
//    把用于检索的数据内容 都放入all
//    例如： 分类名字 ，品牌名字，name，
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all;
    //分类id
    private Long categoryId ;
    //品牌id
    private Long brandId ;

    private Long createTime;//
    //[4499,4999]
    private Set<Long> price ;
    //规格参数{名字：值}
    private Map<String,Object> specs ;
}
