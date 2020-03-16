package com.leyou.search.po;

import lombok.Data;

/**
 * 搜索返回的对象
 */
@Data
public class GoodsDTO {
    private Long id;
    private String skus;
    private String subTitle;
}
