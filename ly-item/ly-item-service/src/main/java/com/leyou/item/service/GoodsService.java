package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;
@Service
public class GoodsService {

    @Autowired
    private TbSpuService tbSpuService;
    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        //设置分页的参数
        IPage<TbSpu> page1 = new Page<>(page,rows);
        //设置查询条件
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isBlank(key)){
            queryWrapper.lambda().like(TbSpu::getName,key);
        }
        if(saleable != null){
            queryWrapper.lambda().eq(TbSpu::getSaleable,saleable);
        }
        //查询结果
        IPage<TbSpu> tbSpuIPage = tbSpuService.page(page1, queryWrapper);
        if(tbSpuIPage == null || CollectionUtils.isEmpty(tbSpuIPage.getRecords())){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //转换对象
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(tbSpuIPage.getRecords(), SpuDTO.class);
        //获取品牌名字和分类名字
        handleBrandNameAndCategoryName(spuDTOList);

        //返回pageresult
        return new PageResult(tbSpuIPage.getTotal(),
                tbSpuIPage.getPages(),
                spuDTOList);
    }

    @Autowired
    private TbBrandService brandService;
    @Autowired
    private TbCategoryService categoryService;

    /**
     * 查询 品牌和 分类的名字
     * @param spuDTOList
     */
    private void handleBrandNameAndCategoryName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) {
            //品牌id
            Long brandId = spuDTO.getBrandId();
            TbBrand tbBrand = brandService.getById(brandId);
            spuDTO.setBrandName(tbBrand.getName());

            //获取cid的集合
            List<Long> categoryIds = spuDTO.getCategoryIds();
            Collection<TbCategory> tbCategoryCollection = categoryService.listByIds(categoryIds);
            // 2、表达式
            String categoryName = tbCategoryCollection.
                    stream().
                    map(TbCategory::getName).
                    collect(Collectors.joining("/"));
            //分类1/分类2/分类3
            spuDTO.setCategoryName(categoryName);
        }
    }


    @Autowired
    private TbSpuDetailService tbSpuDetailService;
    @Autowired
    private TbSkuService tbSkuService;
    /**
     * 新增spu
     * @param spuDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(SpuDTO spuDTO) {

        //保存spu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean b = tbSpuService.save(tbSpu);
        if(!b){
           throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //获取spu的主键id
        Long spuId = tbSpu.getId();
        //获取spuDetailDTO
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        //把dto转tb
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        //设置主键ID
        tbSpuDetail.setSpuId(spuId);
        //保存spu_detail
        boolean b1 = tbSpuDetailService.save(tbSpuDetail);
        if(!b1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //获取skuDTO 集合
        List<SkuDTO> skuDTOList = spuDTO.getSkus();
        //转化对象
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(skuDTOList, TbSku.class);
        //循环设置spuid
        for (TbSku tbSku : tbSkuList) {
            tbSku.setSpuId(spuId);
        }
        //批量保存sku,spu 和sku 是一对多关系
        boolean b2 = tbSkuService.saveBatch(tbSkuList);
        if(!b2){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 修改上下架状态
     * @param spuId
     * @param saleable
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleable(Long spuId, Boolean saleable) {
        //修改spu的状态
        TbSpu tbSpu = new TbSpu();
        tbSpu.setId(spuId);
        tbSpu.setSaleable(saleable);
        boolean b = tbSpuService.updateById(tbSpu);
        if(!b){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //修改sku的状态
        UpdateWrapper<TbSku> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TbSku::getSpuId,spuId);
        updateWrapper.lambda().set(TbSku::getEnable,saleable);
        boolean b1 = tbSkuService.update(updateWrapper);
        if(!b1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        String tags = saleable? ITEM_UP_TAGS: ITEM_DOWN_TAGS;
//        topic:tags
        String dest = ITEM_TOPIC_NAME + ":"+tags;
//        RocketMQ消息
        rocketMQTemplate.convertAndSend(dest,spuId);
    }
    /**
     * 根据spuid 查询spudetail对象
     * @param spuId
     * @return
     */
    public SpuDetailDTO findSpuDetailBySpuId(Long spuId) {
        TbSpuDetail tbSpuDetail = tbSpuDetailService.getById(spuId);
        if(tbSpuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpuDetail,SpuDetailDTO.class);
    }

    /**
     * 根据spuid  查询sku集合
     * @param spuId
     * @return
     */
    public List<SkuDTO> findSkuListBySpuId(Long spuId) {
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        List<TbSku> tbSkuList = tbSkuService.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbSkuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSkuList,SkuDTO.class);
    }

    /**
     * 修改商品信息
     * @param spuDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuDTO spuDTO) {
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        //修改spu
        boolean b = tbSpuService.updateById(tbSpu);
        if(!b){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), TbSpuDetail.class);
        //修改spuDetail
        boolean b1 = tbSpuDetailService.updateById(tbSpuDetail);
        if(!b1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        // 删除sku
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuDTO.getId());
        boolean b2 = tbSkuService.remove(queryWrapper);
        if(!b2){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        List<SkuDTO> skuDTOList = spuDTO.getSkus();
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(skuDTOList, TbSku.class);
        for (TbSku tbSku : tbSkuList) {
            tbSku.setSpuId(spuDTO.getId());
        }
        //修改sku
        boolean b3 = tbSkuService.saveBatch(tbSkuList);
        if(!b3){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }
    /**
     * 根据主键id 查询spu对象
     * @param spuId
     * @return
     */
    public SpuDTO findSpuById(Long spuId) {
        TbSpu tbSpu = tbSpuService.getById(spuId);
        if(tbSpu == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpu,SpuDTO.class);
    }
}
