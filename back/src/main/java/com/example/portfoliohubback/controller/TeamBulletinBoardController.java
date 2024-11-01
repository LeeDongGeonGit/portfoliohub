package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.controller.request.BoardRequest;
import com.example.portfoliohubback.controller.response.ApiResponse;
import com.example.portfoliohubback.controller.response.BoardResponse;
import com.example.portfoliohubback.controller.response.ResponseCode;
import com.example.portfoliohubback.entity.TeamBulletinBoardEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.TeamBullentinBoardRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/teamBulletinBoard")
@RequiredArgsConstructor
public class TeamBulletinBoardController {
    @Autowired
    private TeamBullentinBoardRepository teamBullentinBoardRepository;
    @Autowired
    private UserRepository userRepository;
    private String getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }
    // 게시판 생성
    @PostMapping
        public ApiResponse<BoardResponse.boardOne> createboard(@RequestBody BoardRequest.Create board,
                                                               @RequestHeader("Authorization") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.BAD_REQUEST, null);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uId = null;
        if (authentication != null) {
            uId = authentication.getName();
        }
        UserEntity user = userRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        TeamBulletinBoardEntity newTeamBulletinBoard = new TeamBulletinBoardEntity();
        newTeamBulletinBoard.setProjectName(board.getProjectName());
        newTeamBulletinBoard.setUser(user);
        newTeamBulletinBoard.setProjectStartDate(board.getProjectStartDate());
        newTeamBulletinBoard.setProjectEndDate(board.getProjectEndDate());
        newTeamBulletinBoard.setProjectLocal(board.getProjectLocal());
        newTeamBulletinBoard.setProjectMemberCount(board.getProjectMemberCount());
        newTeamBulletinBoard.setProjectDevelopmentField(board.getProjectDevelopmentField());
        newTeamBulletinBoard.setProjectDescription(board.getProjectDescription());

        TeamBulletinBoardEntity save = teamBullentinBoardRepository.save(newTeamBulletinBoard);
        BoardResponse.boardOne of = BoardResponse.boardOne.of(save);
        return ApiResponse.response(true, ResponseCode.Created, of);
    }

    // 게시판 목록 가져오기(페이징처리 완료)
    @GetMapping
    public ApiResponse<BoardResponse.boardList> getBoardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TeamBulletinBoardEntity> boardPage = teamBullentinBoardRepository.findAll(pageable);

        BoardResponse.boardList boardList = BoardResponse.boardList.of(boardPage);

        return ApiResponse.response(
                true,
                ResponseCode.SUCCESS,
                boardList
        );
    }

    // 필터링된 게시판 목록 가져오기(페이징처리 완료)
    @GetMapping("/Filtered")
    public ApiResponse<BoardResponse.boardList> getBoardFilteredList(
            @RequestHeader("Authorization") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        // 사용자 아이디 확인
        userId = getUserIdFromToken();

        if (userId == null || userId.isEmpty()) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 사용자 ID에 해당하는 게시글만 가져오기
        Page<TeamBulletinBoardEntity> boardPage = teamBullentinBoardRepository.findByUserId(userId, pageable);

        BoardResponse.boardList boardList = BoardResponse.boardList.of(boardPage);

        return ApiResponse.response(
                true,
                ResponseCode.SUCCESS,
                boardList
        );
    }


    // 게시글 검색하기
    @GetMapping("/search")
    public ApiResponse<BoardResponse.boardList> getSearchingBoardList(
            @RequestParam("term") String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TeamBulletinBoardEntity> boardPage = teamBullentinBoardRepository.findByProjectDevelopmentFieldContaining(term, pageable);

        BoardResponse.boardList boardList = BoardResponse.boardList.of(boardPage);

        return ApiResponse.response(true, ResponseCode.SUCCESS, boardList);
    }



    @GetMapping("/list")
    public ApiResponse<List<BoardResponse.boardOne>> getBoardListAll() {
        List<TeamBulletinBoardEntity> boardListAll = teamBullentinBoardRepository.findAll();

        List<BoardResponse.boardOne> boardListResponse = new ArrayList<>();
        for (TeamBulletinBoardEntity entity : boardListAll) {
            BoardResponse.boardOne boardOneResponse = BoardResponse.boardOne.of(entity);
            boardListResponse.add(boardOneResponse);
        }
        return ApiResponse.response(
                true,
                ResponseCode.SUCCESS,
                boardListResponse
        );
    }
    //게시판 하나 상세보기
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse.boardOne> getEventsByDay(@PathVariable("boardId") Long boardId) {
        String userId = getUserIdFromToken();
        System.out.println("userId 이거야 : "+userId);
//        if (userId == null) {
//            System.out.println("아이디 받아온 값 : " + userId);
//            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
//        }

        // 해당 eventId를 가진 이벤트를 가져오기 위해 Repository를 사용
        Optional<TeamBulletinBoardEntity> byId = teamBullentinBoardRepository.findById(boardId);

        if (!byId.isPresent()) {
            // eventId에 해당하는 이벤트가 없는 경우
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 이벤트를 응답용 DTO로 변환
        TeamBulletinBoardEntity board = byId.get();
        BoardResponse.boardOne boardResponseOne = BoardResponse.boardOne.of(board);

        if(userId.equals(board.getUser().getId())){
            boardResponseOne.setMine(true);
        }
        else {
            boardResponseOne.setMine(false);
        }
        // 변환된 DTO를 ApiResponse로 감싸서 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, boardResponseOne);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ApiResponse<String> deleteEvent(@PathVariable("boardId") Long boardId) {

        String userId = getUserIdFromToken();

        if (userId == null) {
            System.out.println("아이디 받아온 값 : " + userId);
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }


        // 해당 boardId를 가진 이벤트를 가져오기 위해 Repository를 사용
        Optional<TeamBulletinBoardEntity> byId = teamBullentinBoardRepository.findById(boardId);

        if (!byId.isPresent()) {
            // boardId에 해당하는 이벤트가 없는 경우
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 게시판이 해당 사용자의 것인지 확인
        TeamBulletinBoardEntity board = byId.get();
        if (!board.getUser().getId().equals(userId)) {
            // 게시판의 소유자가 다른 경우
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        // board 삭제
        teamBullentinBoardRepository.delete(board);

        // 삭제 성공 메시지 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, "삭제를 완료했습니다.");
    }

    // 게시물 하나 수정
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponse.boardOne> updateEvent(@PathVariable("boardId") Long boardId,
                                                           @RequestBody BoardRequest.Update boardUpdate) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        Optional<TeamBulletinBoardEntity> byId = teamBullentinBoardRepository.findById(boardId);

        if (!byId.isPresent()) {
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        TeamBulletinBoardEntity board = byId.get();
        // 권환 있는지 보는거임
        if (!board.getUser().getId().equals(userId)) {
            return ApiResponse.response(false, ResponseCode.FORBIDDEN, null);
        }

        board.setProjectName(boardUpdate.getProjectName());
        board.setProjectStartDate(boardUpdate.getProjectStartDate());
        board.setProjectEndDate(boardUpdate.getProjectEndDate());
        board.setProjectLocal(boardUpdate.getProjectLocal());
        board.setProjectMemberCount(boardUpdate.getProjectMemberCount());
        board.setProjectDevelopmentField(boardUpdate.getProjectDevelopmentField());
        board.setProjectDescription(boardUpdate.getProjectDescription());

        TeamBulletinBoardEntity updatedBoard = teamBullentinBoardRepository.save(board);
        BoardResponse.boardOne boardResponseOne = BoardResponse.boardOne.of(updatedBoard);
        boardResponseOne.setMine(true);
        return ApiResponse.response(true, ResponseCode.SUCCESS, boardResponseOne);
    }
}
