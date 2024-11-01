package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.controller.request.FollowerRequest;
import com.example.portfoliohubback.controller.response.FollowerResponse;
import com.example.portfoliohubback.controller.response.UserResponse;
import com.example.portfoliohubback.entity.BanEntity;
import com.example.portfoliohubback.entity.FollowerEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.BanRepository;
import com.example.portfoliohubback.repository.FollowerRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequiredArgsConstructor
@RequestMapping("/follower")
public class FollowerController {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final BanRepository banRepository;


    //팔로워 추가
    @PostMapping
    public ResponseEntity create(@RequestBody FollowerRequest.Create follower) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        UserEntity followee = userRepository.findById(follower.getId()).get();
        Optional<FollowerEntity> isFollower = followerRepository.findByUserAndFollowee(savedUser,followee);
        if(isFollower.isPresent()) {
            return ResponseEntity.badRequest().body("이미 팔로우가 존재합니다.");
        }
        else if(savedUser.equals(followee)){
            return ResponseEntity.badRequest().body("본인 입니다.");
        }
        else {
        FollowerEntity newFollower = new FollowerEntity();
        newFollower.setUser(savedUser);
        newFollower.setFollowee(followee);
        newFollower.setLookFollowee(true);
        followerRepository.save(newFollower);
        return ResponseEntity.ok("팔로우 추가를  완료했습니다.");
        }
    }
    @GetMapping("/user/{name}/{page}")
    public ResponseEntity findUserByName(@PathVariable String name, @PathVariable int page) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        List<BanEntity> banEntityList = banRepository.findByFromUserOrToUser(savedUser,savedUser);
        List<String> idList = new ArrayList<>();
        for(BanEntity ban : banEntityList){
            if(!ban.getFromUser().getId().equals(id)){
                idList.add(ban.getFromUser().getId());
            }
            else {
                idList.add(ban.getToUser().getId());
            }
        }
        System.out.println(idList.toString());

        return ResponseEntity.ok(UserResponse.PageList.of(
                userRepository.findByNameContainingAndIdNotIn(name,idList,pageRequest)));
    }

    //팔로우 목록 얻기(10개씩 페이징)
    @GetMapping("/{page}")
    public ResponseEntity getFollower(@PathVariable int page) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        return ResponseEntity.ok(FollowerResponse.PageList.of(followerRepository.findByUser(savedUser,pageRequest)));
    }
    //팔로위 목록 얻기(10개씩 페이징)
    @GetMapping("/followee/{page}")
    public ResponseEntity getFollowee(@PathVariable int page) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        return ResponseEntity.ok(
                FollowerResponse.PageList.of(
                        followerRepository.findByFolloweeAndLookFollowee(savedUser,true,pageRequest)));
    }
    //팔로우 목록 이름으로 검색 얻기(10개씩 페이징)
    @GetMapping("/{name}/{page}")
    public ResponseEntity findFollowerByName(@PathVariable String name,@PathVariable int page) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        return ResponseEntity.ok(FollowerResponse.PageList.of(followerRepository.findByUserAndFolloweeNameContaining(savedUser,name,pageRequest)));
    }
    //팔로위 목록 이름으로 검색 얻기(10개씩 페이징)
    @GetMapping("/followee/{name}/{page}")
    public ResponseEntity findFolloweeByName(@PathVariable String name,@PathVariable int page) {
        String id =SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity savedUser =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        return ResponseEntity.ok(
                FollowerResponse.PageList.of(
                        followerRepository.findByFolloweeAndLookFolloweeAndUserNameContaining(savedUser,true,name,pageRequest)));
    }


    // 팔로워 삭제(db에 삭제됨)
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFollower(@PathVariable long id) {
        String userId =SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<FollowerEntity> userOptional = followerRepository.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("찾을 수 없는 유저입니다.");
        }
        else if(!userOptional.get().getUser().getId().equals(userId)){
            return ResponseEntity.ok("본인만 삭제할 수 있습니다.");
        }
        else {
            followerRepository.deleteById(id);
            return ResponseEntity.ok().body("팔로루 삭제를 완료했습니다.");
        }
    }
    // 팔로위 삭제(look_followee가 false로 버뀜)
    @DeleteMapping("/followee/{id}")
    public ResponseEntity deleteFollowee(@PathVariable long id) {
        Optional<FollowerEntity> userOptional = followerRepository.findById(id);
        String userId =SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("찾을 수 없는 유저입니다.");
        }
        else if(!userOptional.get().getFollowee().getId().equals(userId)){
            return ResponseEntity.ok("본인만 삭제할 수 있습니다.");
        }
        else {
            FollowerEntity saved = userOptional.get();
            saved.setLookFollowee(false);
            followerRepository.save(saved);
            return ResponseEntity.ok().body("팔로위 삭제를 완료했습니다.");
        }
    }
}
