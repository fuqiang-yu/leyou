package com.leyou.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8
 * </p>
 *
 * @author HM
 * @since 2020-02-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpuDTO {


    /**
     * spu id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题，一般是促销信息
     */
    private String subTitle;

    /**
     * 1级类目id
     */
    private Long cid1;

    /**
     * 2级类目id
     */
    private Long cid2;

    /**
     * 3级类目id
     */
    private Long cid3;

    /**
     * 商品所属品牌id
     */
    private Long brandId;

    /**
     * 是否上架，0下架，1上架
     */
    private Boolean saleable;

    private String brandName;

    private String categoryName;

    private SpuDetailDTO spuDetail;

    private List<SkuDTO> skus;

    private Date createTime;
    /**
     * 返回 分类id 的集合
     * @return
     */
    @JsonIgnore //序列化时，不会对这个方法进行序列化
    public List<Long> getCategoryIds(){
        return Arrays.asList(cid1,cid2,cid3);
    }


}
