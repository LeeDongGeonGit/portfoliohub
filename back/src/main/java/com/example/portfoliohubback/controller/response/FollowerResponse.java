package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.FollowerEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

public class FollowerResponse {
    @Data
    @Builder
    public static class Detail{
        private Long id;
        private UserResponse.Detail user;
        private UserResponse.Detail followee;
        public static Detail of(FollowerEntity follower){
            return Detail.builder()
                    .id(follower.getId())
                    .user(UserResponse.Detail.of(follower.getUser()))
                    .followee(UserResponse.Detail.of(follower.getFollowee()))
                    .build();
        }
    }
    @Data
    @Builder
    public static class PageList{
        //목록사이즈
        private int size;
        private List<Detail> list;
        public static FollowerResponse.PageList of(Page<FollowerEntity> followerEntityPage){
            return PageList.builder()
                    .list(followerEntityPage.getContent().stream().map(Detail::of).toList())
                    .size(followerEntityPage.getTotalPages())
                    .build();
        }
    }

}
