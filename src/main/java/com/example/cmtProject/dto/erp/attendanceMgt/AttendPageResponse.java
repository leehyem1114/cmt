package com.example.cmtProject.dto.erp.attendanceMgt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@lombok.Data
public class AttendPageResponse {
    private boolean result;
    private Data data;

    public AttendPageResponse(List<AttendDTO> contents, int page, long totalCount) {
        this.result = true;
        this.data = new Data(contents, page, totalCount);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private List<AttendDTO> contents;
        private Pagination pagination;

        public Data(List<AttendDTO> contents, int page, long totalCount) {
            this.contents = contents;
            this.pagination = new Pagination(page, totalCount);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Pagination {
        private int page;
        private long totalCount;

        public Pagination(int page, long totalCount) {
            this.page = page;
            this.totalCount = totalCount;
        }
    }
}