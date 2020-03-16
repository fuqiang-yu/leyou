package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-02-29
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {
    //根据品牌的id，查询中间表获取分类的list集合
    @Select("SELECT tc.id,tc.`name`,tc.parent_id,tc.sort FROM `tb_category_brand` tcb, tb_category tc\n" +
            "WHERE tcb.category_id = tc.id AND tcb.brand_id = #{id}")
    List<TbCategory> selectCategoryByBrandId(@Param("id") Long id);
}
