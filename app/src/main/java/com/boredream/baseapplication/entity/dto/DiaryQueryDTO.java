package com.boredream.baseapplication.entity.dto;


public class DiaryQueryDTO extends PageParamDTO {

	// 查询日期(年-月)", example = "2021-12
    private String queryDate;

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }
}
