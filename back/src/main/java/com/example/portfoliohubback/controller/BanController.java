package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.controller.request.BanRequest;
import com.example.portfoliohubback.controller.response.BanResponse;
import com.example.portfoliohubback.entity.BanEntity;
import com.example.portfoliohubback.entity.FollowerEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.BanRepository;
import com.example.portfoliohubback.repository.FollowerRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequiredArgsConstructor
@RequestMapping("/ban")
public class BanController {
    private final BanRepository banRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    @PostMapping
    public ResponseEntity createBan(@RequestBody BanRequest.Create requestedBan){
        try{
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity from_User =  userRepository.findById(id).orElseThrow(() -> new RuntimeException("로그인한 사용자를 찾을 수 없습니다."));
            UserEntity to_User =  userRepository.findById(requestedBan.getId()).orElseThrow(() -> new RuntimeException("요청된 사용자를 찾을 수 없습니다."));
            Optional<FollowerEntity> follower = followerRepository.findByUserAndFollowee(from_User,to_User);
            if (follower.isPresent()) {
                followerRepository.delete(follower.get());
            }
            BanEntity newBan = new BanEntity();
            newBan.setFromUser(from_User);
            newBan.setToUser(to_User);
            banRepository.save(newBan);
            return ResponseEntity.ok("유저를 차단했습니다.");
        } catch (Exception e){
            return ResponseEntity.status(400).body("존재하지 않는 아이디");
        }
    }
    @GetMapping("/{page}")
    public ResponseEntity getBanList(@PathVariable int page){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user =  userRepository.findById(id).get();
        PageRequest pageRequest = PageRequest.of(page-1,10);
        return ResponseEntity.ok(BanResponse.PageList.of(banRepository.findByFromUser(user,pageRequest)));
    }
    @DeleteMapping("/{banId}")
    public ResponseEntity deleteBan(@PathVariable Long banId){
        try {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            BanEntity ban = banRepository.findById(banId).get();
            if(!ban.getFromUser().getId().equals(id)){
                return ResponseEntity.ok("본인만 삭제할 수 있습니다.");
            }
            else {
                banRepository.deleteById(banId);
                return ResponseEntity.ok("차단이 해제되었습니다..");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
}
