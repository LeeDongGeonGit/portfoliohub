package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.UserEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserResponse {
    @Data
    @Builder
    public static class Detail{
        private String id;
        private String img_url;
        private String name;
        private String email;
        public static UserResponse.Detail of(UserEntity user){
            return Detail.builder()
                    .id(user.getId())
                    .img_url(user.getImg_url())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }
    @Data
    @Builder
    public static class PageList{
        //목록사이즈
        private int size;
        private List<UserResponse.Detail> list;
        public static UserResponse.PageList of(Page<UserEntity> userEntityPage){
            return PageList.builder()
                    .list(userEntityPage.getContent().stream().map(UserResponse.Detail::of).toList())
                    .size(userEntityPage.getTotalPages())
                    .build();
        }
    }
}
