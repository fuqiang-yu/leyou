package com.leyou.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组
 * </p>
 *
 * @author HM
 * @since 2020-02-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpecGroupDTO {


    /**
     * 主键
     */
    private Long id;

    /**
     * 商品分类id，一个分类下有多个规格组
     */
    private Long cid;

    /**
     * 规格组的名称
     */
    private String name;


}
