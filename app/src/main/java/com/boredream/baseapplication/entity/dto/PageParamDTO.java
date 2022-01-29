package com.boredream.baseapplication.entity.dto;


/**
 * 分页基础参数
 */
public class PageParamDTO {

	// 页码(不能为空)", example = "1
    protected Integer page;

	// 每页数量(不能为空)", example = "10
    protected Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
