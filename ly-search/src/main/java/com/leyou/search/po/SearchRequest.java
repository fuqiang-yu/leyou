package com.leyou.search.po;


import java.util.Map;

public class SearchRequest {

    /**
     * 用户输入的关键字
     */
    private String key;
    /**
     * 当前页面码
     */
    private Integer page;
    /**
     * 用户选择的过滤项
     */
    private Map<String,String> filter;


    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

    public Map<String, String> getFilter() {
        return filter;
    }
    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
