package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.entity.FolloweeEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.FolloweeRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/followee")
public class FolloweeController {
    @Autowired
    private FolloweeRepository followeeRepository;

    @Autowired
    private UserRepository userRepository;
    // Create

    @PostMapping("/{boardId}")
    public FolloweeEntity create(@PathVariable String boardId, @RequestBody FolloweeEntity followee) {
        UserEntity board = userRepository.findById(boardId).orElse(null);
        followee.setUser(board);
        return followeeRepository.save(followee);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        Optional<FolloweeEntity> userOptional = followeeRepository.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"해당 id를 가진 계정이 없습니다.\"}");
        } else {
            followeeRepository.deleteById(id);
            return ResponseEntity.ok().body("{\"message\": \"삭제를 성니다.\"}");
        }
    }
}
