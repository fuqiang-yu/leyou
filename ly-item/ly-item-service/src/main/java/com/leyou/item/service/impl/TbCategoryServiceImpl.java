package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-02-29
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Autowired
    private TbCategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> findByParentId(Long pid) {
        /**
         * 根据pid查询该分类下的所有分类对象
         */

        //1.准备查询对象
        TbCategory category = new TbCategory();
        category.setParentId(pid);
        //QueryWrapper中的参数可以传入封装要查询的对象
        QueryWrapper<TbCategory> wrapper = new QueryWrapper<>(category);
       //wrapper.eq("parent_id", pid);

        //2.通过mapper接口完成查询
        List<TbCategory> categories = categoryMapper.selectList(wrapper);

        //通过工具类提供的BeanHelper完成类的转换
        //参数1 要转换的类，参数2 转换的类的类型
        //3.转换类型
        return BeanHelper.copyWithCollection(categories, CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> findListByBrandId(Long brandId) {
        //单表查询不能完成
        List<TbCategory> categories = categoryMapper.selectCategoryByBrandId(brandId);

        //判断是否查找到分类，否则抛异常
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        //通过BeanHelper转成CategoryDTO
        return BeanHelper.copyWithCollection(categories, CategoryDTO.class);
    }
}
