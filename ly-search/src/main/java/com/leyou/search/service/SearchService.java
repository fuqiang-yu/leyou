package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import com.leyou.search.po.Goods;
import com.leyou.search.po.GoodsDTO;
import com.leyou.search.po.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import com.lytou.item.client.ItemClient;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private GoodsRepository repository;
    /**
     * 通过Spu对象的值，构造Goods对象
     * 可以用来导入es的数据
     * @return
     */
    public Goods createGoods(SpuDTO spuDTO){

        Long spuId = spuDTO.getId();
//        商品名称 ，品牌名字  ，分类的名字
        String all = spuDTO.getName()+","+spuDTO.getBrandName()+","+spuDTO.getCategoryName();
//         使用feign的远程调用，根据spuid查询sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
//        构造 es中skus里面的内容，name-val ,使用json格式
//        只要sku中的4个值id,title,price,image
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        Set<Long> priceSet = new HashSet<>();
        for (SkuDTO skuDTO : skuDTOList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",skuDTO.getId());
            map.put("title",skuDTO.getTitle());
            map.put("price",skuDTO.getPrice());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
            skuMapList.add(map);
            priceSet.add(skuDTO.getPrice());
        }
//        使用流的方法 构造价格集合
//        Set<Long> priceSet = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
//         发起远程调用，查询规格参数名字，注意：要查用于搜索的规格参数
        List<SpecParamDTO> paramDTOList = itemClient.findSpecParamsList(null, spuDTO.getCid3(), true);
//         查询规格参数值
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailBySpuId(spuId);
//        通用的规格参数 值，json字符串，转map ，key- 规格参数id val-规格参数的值
        String genericSpecJson = spuDetailDTO.getGenericSpec();
        Map<Long, Object> genericMap = JsonUtils.toMap(genericSpecJson, Long.class, Object.class);
//        特有的规格参数值，json字符串,转map，key- id,val - 数组
        String specialSpecJson = spuDetailDTO.getSpecialSpec();
        Map<Long,List<String>> specialMap = JsonUtils.nativeRead(specialSpecJson, new TypeReference<Map<Long, List<String>>>() {
        });

//      把规格参数的名字和值对应整合成map
        Map<String,Object> specs = new HashMap<>();
        for (SpecParamDTO specParamDTO : paramDTOList) {
            Long id = specParamDTO.getId();
//            规格参数的名字
            String paramName = specParamDTO.getName();
//            规格参数的值
            Object val = null;
            if(specParamDTO.getGeneric()){
//                通用的额规格参数
                val = genericMap.get(id);
            }else{
//                特有参数
                val = specialMap.get(id);
            }
//            判断是否数字类型
            if(specParamDTO.getIsNumeric()){
//                把数字类型的一些值，转成区间值，如 iphone11 的电池容量是 3700mah  转后  3500mah-4000mah
                val = chooseSegment(val, specParamDTO);
            }
            specs.put(paramName,val);
        }

        Goods goods = new Goods();
//        spuid
        goods.setId(spuDTO.getId());
//        品牌id
        goods.setBrandId(spuDTO.getBrandId());
//        分类id
        goods.setCategoryId(spuDTO.getCid3());
//        促销方式
        goods.setSubTitle(spuDTO.getSubTitle());
//        创建时间
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
//        把所用用来检索的内容放在ALL
        goods.setAll(all);
//        spu下 sku的价格集合 set,不重复
        goods.setPrice(priceSet);
//        spu下 sku的集合,用来显示,使用字符串，里面是json数据
        goods.setSkus(JsonUtils.toString(skuMapList));
//        商品对应的规格参数的名字和值
        goods.setSpecs(specs);
        return goods;

    }


    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段  0-2000,2000-3000,3000-4000,4000-
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    @Autowired
    private ElasticsearchTemplate esTemplate;
    /**
     * 根据用户输入的关键词进行查询
     * @return
     */
    public PageResult<GoodsDTO> search(SearchRequest searchRequest){
//        用户输入的内容
        String key = searchRequest.getKey();
//        构造原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        设置返回的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},
                null));
//        设置关键词搜索
        queryBuilder.withQuery(basicQuery(searchRequest));
//        设置分页
//        当前页码，从0 开始
        Integer page = searchRequest.getPage()-1;
//        每页显示条数
        Integer size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page,size));
//        把搜索条件发送到es服务器，并接收返回结果
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        获取搜索结果集合
        List<Goods> goodsList = aggregatedPage.getContent();
        if(CollectionUtils.isEmpty(goodsList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<GoodsDTO> goodsDTOList = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);
//        处理返回结果
        return new PageResult<GoodsDTO>(aggregatedPage.getTotalElements(),
                Long.valueOf(String.valueOf(aggregatedPage.getTotalPages())),
                goodsDTOList);
    }

    /**
     * 获取过滤条件
     * 先做关键词查询
     * 再聚合
     * @param searchRequest
     * @return
     */
    public Map<String, List<?>> searchFilter(SearchRequest searchRequest) {
//        返回的过滤集合
        Map<String,List<?>> filterMap = new LinkedHashMap<>();
//        获取用户输入的内容
        String key = searchRequest.getKey();
//        构造原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        设置返回的字段,聚合操作 我们不关心关键词搜索返回的内容
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
//        设置关键词
        queryBuilder.withQuery(basicQuery(searchRequest));
//        设置分页
        queryBuilder.withPageable(PageRequest.of(0,1));
//        设置聚合
//         品牌的聚合
        String brandAggName = "brandAggs";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
//        分类的聚合
        String categoryAggName = "categoryAggs";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("categoryId"));
//        把查询的条件 发送给es
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
////        处理返回的结果
        Aggregations aggregations = aggregatedPage.getAggregations();
//        获取 分类聚合的结果,不要使用接口，要使用实现类。 根据聚合结果的数据类型，如：Long，根据聚合时使用的类型 如：Terms
        LongTerms categoryAgg = aggregations.get(categoryAggName);
        List<Long> categoryIds = handlerCategoryName(categoryAgg,filterMap);
//        获取品牌的聚合结果
        LongTerms brandAgg = aggregations.get(brandAggName);
        handlerBrandName(brandAgg,filterMap);
        if(!CollectionUtils.isEmpty(categoryIds) && categoryIds.size()==1){
            //只有当分类是一个内容的时候，再做规格参数的聚合
//            重新做一次 对规格参数的聚合查询
            handlerSpecAgg(searchRequest,categoryIds.get(0),filterMap);
        }
        return filterMap;
    }

    /**
     * 规格参数的聚合
     * @param searchRequest
     * @param categoryId
     * @param filterMap
     */
    private void handlerSpecAgg(SearchRequest searchRequest, Long categoryId, Map<String, List<?>> filterMap) {

        String key = searchRequest.getKey();
//        构造原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        设置返回的字段,聚合操作 我们不关心关键词搜索返回的内容
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
//        设置关键词
        queryBuilder.withQuery(basicQuery(searchRequest));
//        设置分页
        queryBuilder.withPageable(PageRequest.of(0,1));
//        远程调用item，获取用于搜索的规格参数
        List<SpecParamDTO> specParamsList = itemClient.findSpecParamsList(null, categoryId, true);
        for (SpecParamDTO specParamDTO : specParamsList) {
//            规格参数的名字
            String specName = specParamDTO.getName();
//            es中的字段名字
            String fieldName = "specs."+specName;
//        设置聚合条件 字段名字  specs.规格参数名字
            queryBuilder.addAggregation(AggregationBuilders.terms(specName).field(fieldName));
        }
//        发送请求给es
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        处理返回的结果，获取聚合的内容
        Aggregations aggregations = aggregatedPage.getAggregations();
//        获取每一个聚合的结果
        for (SpecParamDTO specParamDTO : specParamsList) {
            String specName = specParamDTO.getName();
            StringTerms specAgg = aggregations.get(specName);
            List<StringTerms.Bucket> buckets = specAgg.getBuckets();
            List<String> specAggList = buckets.stream()
                    .map(StringTerms.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            filterMap.put(specName,specAggList);
        }
    }

    /**
     * 基础查询
     * 包含 必须要做的match  、可能要做filter
     * @param searchRequest
     */
    private BoolQueryBuilder basicQuery(SearchRequest searchRequest){

        String key = searchRequest.getKey();
//        构造bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        添加match
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
//        获取用户选择的过滤条件
        Map<String, String> filter = searchRequest.getFilter();
        if(!CollectionUtils.isEmpty(filter)){
            for (String filterName : filter.keySet()) {
//                字段名字
                String fieldName = "specs."+filterName;
                if(filterName.equals("分类")){
                    fieldName = "categoryId";
                }
                else if(filterName.equals("品牌")){
                    fieldName = "brandId";
                }
                String value = filter.get(filterName);
//             添加过滤条件
                boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName,value));
            }
        }
        return boolQueryBuilder;
    }
    /**
     * 聚合品牌的结果处理
     * @param brandAgg
     * @param filterMap
     */
    private void handlerBrandName(LongTerms brandAgg, Map<String, List<?>> filterMap) {

        //获取聚合的结果，组成品牌id的集合
        List<LongTerms.Bucket> buckets = brandAgg.getBuckets();
        List<Long> brandIds = buckets.stream()
                .map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
//       远程调用 用品牌id集合 获取 品牌对象的集合
        List<BrandDTO> brandDTOList = itemClient.findBrandListByIds(brandIds);
        filterMap.put("品牌",brandDTOList);
    }

    /**
     * 根据分类的聚合结果 获取分类的对象集合
     * @param categoryAgg
     * @param filterMap
     */
    private List<Long> handlerCategoryName(LongTerms categoryAgg, Map<String, List<?>> filterMap) {
         List<LongTerms.Bucket> buckets = categoryAgg.getBuckets();
//        分类的id 集合
//        List<Long> categoryIds = new ArrayList<>();
//        for (LongTerms.Bucket bucket : buckets) {
//            long categoryId = bucket.getKeyAsNumber().longValue();
//            categoryIds.add(categoryId);
//        }
//        用流来处理
        List<Long> categoryIds = buckets.stream().map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
//        远程调用 用分类的id集合获取分类的name集合
        List<CategoryDTO> categoryDTOList = itemClient.findCategoryListByIds(categoryIds);
        filterMap.put("分类",categoryDTOList);
        return categoryIds;
    }

    /**
     * 创建索引
     * @param spuId
     */
    public void createIndex(Long spuId) {
//        获取spu对象
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        构造goods对象
        Goods goods = this.createGoods(spuDTO);
//        创建索引
        repository.save(goods);
    }

    /**
     * 删除索引
     * @param spuId
     */
    public void deleteIndex(Long spuId) {
        repository.deleteById(spuId);
    }
}
