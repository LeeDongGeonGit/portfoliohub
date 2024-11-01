package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.BanEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

public class BanResponse {
    @Data
    @Builder
    public static class Detail{
        private Long id;
        private UserResponse.Detail fromUser;
        private UserResponse.Detail toUser;
        public static BanResponse.Detail of(BanEntity ban){
            return Detail.builder()
                    .id(ban.getBanId())
                    .fromUser(UserResponse.Detail.of(ban.getFromUser()))
                    .toUser(UserResponse.Detail.of(ban.getToUser()))
                    .build();
        }
    }
    @Data
    @Builder
    public static class PageList{
        //목록사이즈
        private int size;
        private List<BanResponse.Detail> list;
        public static BanResponse.PageList of(Page<BanEntity> banEntityPage){
            return BanResponse.PageList.builder()
                    .list(banEntityPage.getContent().stream().map(BanResponse.Detail::of).toList())
                    .size(banEntityPage.getTotalPages())
                    .build();
        }
    }

}
