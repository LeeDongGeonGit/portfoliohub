package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.TeamBulletinBoardEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
    public class BoardResponse {
    @Data
    @Builder
    public static class boardOne {
        private Long projectId;
        private String projectName;
        private String userId;
        private LocalDateTime projectStartDate;
        private LocalDateTime projectEndDate;
        private String projectLocal;
        private int projectMemberCount;
        private String projectDevelopmentField;
        private String projectDescription;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean mine;

        public static boardOne of(TeamBulletinBoardEntity teamBulletinBoard) {

            return boardOne.builder()
                    .projectId(teamBulletinBoard.getProjectId())
                    .projectName(teamBulletinBoard.getProjectName())
                    .userId(teamBulletinBoard.getUser().getId())
                    .projectStartDate(teamBulletinBoard.getProjectStartDate())
                    .projectEndDate(teamBulletinBoard.getProjectEndDate())
                    .projectLocal(teamBulletinBoard.getProjectLocal())
                    .projectMemberCount(teamBulletinBoard.getProjectMemberCount())
                    .projectDevelopmentField(teamBulletinBoard.getProjectDevelopmentField())
                    .projectDescription(teamBulletinBoard.getProjectDescription())
                    .createdAt(teamBulletinBoard.getCreatedAt())
                    .updatedAt(teamBulletinBoard.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class boardList {
        private List<boardOne> content;
        private boolean last;
        private int totalPages;
        private long totalElements;
        private int size;
        private int number;

        public static boardList of(Page<TeamBulletinBoardEntity> boardPage) {
            List<boardOne> boardList = boardPage.getContent().stream()
                    .map(boardOne::of)
                    .collect(Collectors.toList());

            return BoardResponse.boardList.builder()
                    .content(boardList)
                    .last(boardPage.isLast())
                    .totalPages(boardPage.getTotalPages())
                    .totalElements(boardPage.getTotalElements())
                    .size(boardPage.getSize())
                    .number(boardPage.getNumber())
                    .build();
        }
    }


}
