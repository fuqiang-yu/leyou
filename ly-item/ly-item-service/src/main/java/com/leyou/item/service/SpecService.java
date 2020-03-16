package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 管理 规格参数组 规格参数
 */
@Service
public class SpecService {

    @Autowired
    private TbSpecGroupService specGroupService;
    @Autowired
    private TbSpecParamService specParamService;

    /**
     * 查询分组集合
     * @param categoryId
     * @return
     */
    public List<SpecGroupDTO> findSpecGroupListByCid(Long categoryId) {
        //构造数据库的查询条件
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        //设置条件
        //使用表达式设置条件
        queryWrapper.lambda().eq(TbSpecGroup::getCid,categoryId);
        List<TbSpecGroup> tbSpecGroupList = specGroupService.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbSpecGroupList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSpecGroupList,SpecGroupDTO.class);
    }

    /**
     * 根据条件查询 规格参数
     *
     * @param categoryId
     * @param groupId
     * @return
     */
    public List<SpecParamDTO> findSpecParamsList(Long groupId,Long categoryId) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        if(groupId != null ){
            queryWrapper.lambda().eq(TbSpecParam::getGroupId,groupId);
        }
        if(categoryId != null){
            queryWrapper.lambda().eq(TbSpecParam::getCid,categoryId);
        }
        //查询数据
        List<TbSpecParam> tbSpecParamList = specParamService.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbSpecParamList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSpecParamList,SpecParamDTO.class);
    }
}
