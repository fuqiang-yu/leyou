package com.leyou.item.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author HM
 * @since 2020-02-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbSpuDetail extends Model<TbSpuDetail> {

private static final long serialVersionUID=1L;

    @TableId(value = "spu_id",type = IdType.INPUT)//input 指用户手动输入
    private Long spuId;

    /**
     * 商品描述信息
     */
    private String description;

    /**
     * 通用规格参数数据
     */
    private String genericSpec;

    /**
     * 特有规格参数及可选值信息，json格式
     */
    private String specialSpec;

    /**
     * 包装清单
     */
    private String packingList;

    /**
     * 售后服务
     */
    private String afterService;

    private Date createTime;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.spuId;
    }

}
