package com.example.auth.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageInfo {
    private int pageNum;
    private int pageSize;
    private long totalElement;
    private int totalPage;

    @Builder
    public PageInfo(int pageNum, int pageSize, long totalElement, int totalPage) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalElement = totalElement;
        this.totalPage = totalPage;
    }
}
