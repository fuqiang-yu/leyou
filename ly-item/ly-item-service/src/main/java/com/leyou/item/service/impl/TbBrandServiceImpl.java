package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.leyou.item.service.TbCategoryBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-02-29
 */
@Service
@Transactional  //注意pom坐标
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {

    @Autowired
    private TbCategoryBrandService categoryBrandService;

    @Override
    public PageResult<BrandDTO> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        //参数中传入pageNo和pageSize，封装page对象，之前mybatis完成分页，通过分页插件xml
        Page<TbBrand> brandPage = new Page<>(page, rows);

        //创建wrapper查询条件  1.模糊搜索，2.排序字段 3.是否升降序
        QueryWrapper<TbBrand> wrapper = new QueryWrapper<>();
        //判断key不为null的时候进行模糊搜索
        wrapper.like(!StringUtils.isEmpty(key), "name", key);

        //如果排序的字段有值，进行升降序的排序
        if( !StringUtils.isEmpty(sortBy)){
            //如果是升序，直接加升序条件，否则加降序条件
            if(desc){
                wrapper.orderByDesc(sortBy);
            }else{
                wrapper.orderByAsc(sortBy);
            }
        }

        //通过page方法进行查询  page 分页对象,wrapper条件查询
        this.page(brandPage, wrapper);

        //获取brandPage对象中的列表，目的是为了转换成TbBrand-》BrandDTO
        List<TbBrand> records = brandPage.getRecords();

        return new PageResult<BrandDTO>(brandPage.getTotal(), brandPage.getPages(), BeanHelper.copyWithCollection(records, BrandDTO.class));
    }

    @Override
    public void saveBrand(BrandDTO brandDTO, List<Long> cids) {

        //判断tbBrand不能为空，否则抛异常
        if(brandDTO==null){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //新增品牌到数据库
        TbBrand tbBrand = BeanHelper.copyProperties(brandDTO, TbBrand.class);

        boolean result = this.save(tbBrand); //保存数据库
        //判断新增是否正常，否则抛异常
        if(!result){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //根据cids新增品牌和分类的中间表
        for (Long cid : cids) {
            //确定有品牌分类中间表的entity，mapper，service
            TbCategoryBrand categoryBrand = new TbCategoryBrand();
            categoryBrand.setBrandId(tbBrand.getId());
            categoryBrand.setCategoryId(cid);
            categoryBrandService.save(categoryBrand);   //保存数据库
        }
    }
}
