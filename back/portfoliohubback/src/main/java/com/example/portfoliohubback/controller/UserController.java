package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.emailapi.EmailNumList;
import com.example.portfoliohubback.emailapi.EmailService;
import com.example.portfoliohubback.emailapi.MailDto;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.jwt.JwtTokenProvider;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private  EmailService emailService;



    // Create
    @PostMapping
    public ResponseEntity create(@RequestBody UserEntity user) {
      ;
        try {
            String Pw = passwordEncoder.encode(user.getPassword());
            user.setPassword(Pw);
            user.setDelete(false);
            userRepository.save(user);
            // 저장 성공 시 true 반환
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            // 저장 실패 시 false 반환
            return ResponseEntity.ok().body(false);
        }
    }

    // Delete
    @DeleteMapping
    public ResponseEntity delete() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findById(id).get();
        if (user.isDelete()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"해당 id를 가진 계정이 없습니다.\"}");
        } else {
            user.setDelete(true);
            userRepository.save(user);
            return ResponseEntity.ok("회원탈퇴를 완료했습니다.");
        }
    }
    @GetMapping("/{id}/checkid")
    public ResponseEntity checkId(@PathVariable String id){
        return ResponseEntity.ok(userRepository.existsById(id));
    }
   /* @GetMapping("/{email}/checkemail")
    public ResponseEntity checkEmail(@PathVariable String email){
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }*/
   @GetMapping("/{email}/{num}/id")
   public ResponseEntity getIdByEmail(@PathVariable String email, @PathVariable String num){
       if(EmailNumList.sameNum(email,num)){
           Optional<UserEntity> user = userRepository.findByEmail(email);
           return ResponseEntity.ok(user.get().getId());

       }else{
           return  ResponseEntity.ok(false);
       }
   }


   @GetMapping("/{email}/checkemail")
   public ResponseEntity checkEmail(@PathVariable String email) {
       if (userRepository.existsByEmail(email)) {
           MailDto mailDto = new MailDto();
           mailDto.setEmailAddr(email);
           mailDto.setEmailTitle(" ");
           String num = generateRandomNumber();
           mailDto.setEmailContent(num);
           emailService.sendSimpleMessage(mailDto);
           EmailNumList.addNum(email, num);
           return ResponseEntity.ok(true);
       } else {
           return ResponseEntity.ok(false);
       }
   }

    @GetMapping("/{email}/checkemail-signup")
    public ResponseEntity checkEmailSignUp(@PathVariable String email) {
        if (!userRepository.existsByEmail(email)) {
            MailDto mailDto = new MailDto();
            mailDto.setEmailAddr(email);
            mailDto.setEmailTitle(" ");
            String num = generateRandomNumber();
            mailDto.setEmailContent(num);
            emailService.sendSimpleMessage(mailDto);
            EmailNumList.addNum(email, num);
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(true);
        }
    }
    @GetMapping("/{email}/{num}/authentication")
    public ResponseEntity emailAuthentication(@PathVariable String email, @PathVariable String num) {
        if (EmailNumList.sameNum(email,num)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }
    @GetMapping("/{email}/{num}/authentication-pw")
    public ResponseEntity emailAuthenticationPw(@PathVariable String email, @PathVariable String num) {
        if (EmailNumList.sameNumNoDelete(email,num)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }
    @PutMapping("/change/pw/{num}")
    public ResponseEntity updatePasswordByEmail(@RequestBody UserEntity user, @PathVariable String num){

        if (EmailNumList.sameNum(user.getEmail(),num)) {
            UserEntity userFind = userRepository.findByEmail(user.getEmail()).get();
            userFind.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userFind);

        return ResponseEntity.ok(true);
       } else {
            System.out.println("실패");
            return ResponseEntity.ok(false);
        }
    }



    // 랜덤한 숫자 생성
    private String generateRandomNumber() {
        Random random = new Random();
        int length = random.nextInt(3) + 4; // 4~6 사이의 길이
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0~9 사이의 숫자
        }
        return sb.toString();
    }
    /*
    @Cacheable(value = "customerCache", key = "#email")
    public String getFromCache(String email) {
        // 캐시에서 해당 이메일의 값 가져오기
        // 캐시에 값이 없으면 null 반환
        return null;
    }

    @CachePut(value = "customerCache", key = "#email")
    public String saveToCache(String email, String value) {
        // 캐시에 값을 저장하는 로직
        return value; // 캐시에 값을 저장
    }

    @CacheEvict(value = "customerCache", key = "#email")
    public void removeFromCache(String email) {
        // 캐시에서 값을 삭제
    }*/



    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserEntity user){
        Optional<UserEntity> loginUser = userRepository.findById(user.getId());
        if(!loginUser.isPresent()){
            System.out.println("존재하는 아이디가 아닙니다.");
            return ResponseEntity.ok(false);
        }
        else if(loginUser.get().isDelete()){
            return ResponseEntity.ok(false);
        }
        else if(!passwordEncoder.matches(user.getPassword(),loginUser.get().getPassword())){
            System.out.println("비밀번호가 일치하지 않습니다.");
            return ResponseEntity.ok(false);
        }
        else {
            String token = jwtTokenProvider.createToken(loginUser.get().getId());
            return ResponseEntity.ok(token);
        }


}
@GetMapping("/mypage")
public ResponseEntity getMyInfo(){
        try {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity savedUser = userRepository.findById(id).get();
            UserEntity response = new UserEntity();
            if(savedUser.getImg_url() == null){
                response.setImg_url(null);
            }else {
                response.setImg_url(savedUser.getImg_url());
            }
            response.setId(savedUser.getId());
            response.setName(savedUser.getName());
            response.setEmail(savedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

}
@PutMapping("/mypage/change/url")
    public ResponseEntity changeImgURL(@RequestBody UserEntity user){
    try {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(id);
        System.out.println(user.getImg_url());
        UserEntity savedUser = userRepository.findById(id).get();
        savedUser.setImg_url(user.getImg_url());
        userRepository.save(savedUser);
        UserEntity response = new UserEntity();
        response.setImg_url(savedUser.getImg_url());
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        // 오류 처리
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
    @PutMapping("/mypage/change/name")
    public ResponseEntity changeName(@RequestBody UserEntity user){
        try {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity savedUser = userRepository.findById(id).get();
            savedUser.setName(user.getName());
            userRepository.save(savedUser);
            UserEntity response = new UserEntity();
            response.setImg_url(savedUser.getImg_url());
            response.setId(savedUser.getId());
            response.setName(savedUser.getName());
            response.setEmail(savedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/mypage/change/pw")
    public ResponseEntity putMyPW(@RequestBody UserEntity user){
        try {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity savedUser = userRepository.findById(id).get();
            savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(savedUser);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






}
