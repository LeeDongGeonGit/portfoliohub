package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.controller.request.PortfolioRequest;
import com.example.portfoliohubback.controller.response.PagedPortfolioResponse;
import com.example.portfoliohubback.controller.response.PortfolioDetailResponse;
import com.example.portfoliohubback.controller.response.PortfolioResponse;
import com.example.portfoliohubback.entity.PortfolioEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.PortfolioRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;
    private String getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }
    @PostMapping
    public ResponseEntity create(@RequestBody PortfolioRequest.Create portfolio) {
        try {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findById(id).get();
            PortfolioEntity portfolioEntity = PortfolioEntity.builder()
                    .career(portfolio.getCareer())
                    .position(portfolio.getPosition())
                    .content(portfolio.getContent())
                    .user(userEntity)
                    .count(0L)
                    .profile(portfolio.getProfile())
                    .build();
            portfolioRepository.save(portfolioEntity);
            // 저장 성공 시 true 반환
            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            // 저장 실패 시 false 반환
            return ResponseEntity.ok().body(false);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDetailResponse> getById(@PathVariable Long id) {
        String userId = getUserIdFromToken();

        Optional<PortfolioEntity> portfolioEntityOptional = portfolioRepository.findById(id);
        if (portfolioEntityOptional.isPresent()) {
            PortfolioEntity portfolioEntity = portfolioEntityOptional.get();

            // count 값을 1 증가시킵니다.
            portfolioEntity.setCount(portfolioEntity.getCount() + 1);
            // 변경된 값을 저장합니다.
            portfolioRepository.save(portfolioEntity);

            // PortfolioDetailResponse 객체 생성 및 설정
            PortfolioDetailResponse portfolioDetailResponse = new PortfolioDetailResponse();
            portfolioDetailResponse.setId(portfolioEntity.getId());
            portfolioDetailResponse.setCreatedAt(portfolioEntity.getCreatedAt().toString());
            portfolioDetailResponse.setPosition(portfolioEntity.getPosition());
            portfolioDetailResponse.setCareer(portfolioEntity.getCareer());
            portfolioDetailResponse.setCount(portfolioEntity.getCount());
            portfolioDetailResponse.setProfile(portfolioEntity.getProfile());
            portfolioDetailResponse.setContent(portfolioEntity.getContent());

            // UserEntity에서 userId를 추출하여 설정
            UserEntity userEntity = portfolioEntity.getUser();
            String ownerId = userEntity.getId();
            portfolioDetailResponse.setUserId(ownerId);
            portfolioDetailResponse.setOwner(ownerId.equals(userId));

            return ResponseEntity.ok(portfolioDetailResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping
    public ResponseEntity<PagedPortfolioResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PortfolioEntity> portfolioPage = portfolioRepository.findAll(pageable);
        List<PortfolioResponse> portfolioResponses = portfolioPage.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PagedPortfolioResponse response = new PagedPortfolioResponse(
                portfolioResponses,
                portfolioPage.isFirst(),
                portfolioPage.isLast(),
                portfolioPage.getTotalPages(),
                portfolioPage.getTotalElements(),
                portfolioPage.getNumber()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedPortfolioResponse> search(
            @RequestParam(required = false) String career,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PortfolioEntity> portfolioPage;

        if (career != null && position != null && userId != null) {
            portfolioPage = portfolioRepository.findByCareerAndPositionAndUser_Id(career, position, userId, pageable);
        } else if (career != null && position != null) {
            portfolioPage = portfolioRepository.findByCareerAndPosition(career, position, pageable);
        } else if (career != null && userId != null) {
            portfolioPage = portfolioRepository.findByCareerAndUser_Id(career, userId, pageable);
        } else if (position != null && userId != null) {
            portfolioPage = portfolioRepository.findByPositionAndUser_Id(position, userId, pageable);
        } else if (career != null) {
            portfolioPage = portfolioRepository.findByCareer(career, pageable);
        } else if (position != null) {
            portfolioPage = portfolioRepository.findByPosition(position, pageable);
        } else if (userId != null) {
            portfolioPage = portfolioRepository.findByUser_Id(userId, pageable);
        } else {
            portfolioPage = portfolioRepository.findAll(pageable);
        }
        List<PortfolioResponse> portfolioResponses = portfolioPage.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PagedPortfolioResponse response = new PagedPortfolioResponse(
                portfolioResponses,
                portfolioPage.isFirst(),
                portfolioPage.isLast(),
                portfolioPage.getTotalPages(),
                portfolioPage.getTotalElements(),
                portfolioPage.getNumber()
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping("/top4")
    public ResponseEntity<List<PortfolioResponse>> getTop4ByCount() {
        // Pageable 객체를 생성하여 상위 4개의 결과만 가져오도록 설정
        Pageable pageable = PageRequest.of(0, 4);
        // 상위 4개의 포트폴리오 엔티티를 가져옴
        List<PortfolioEntity> top4Portfolios = portfolioRepository.findTop4ByOrderByCountDesc(pageable);

        // 엔티티를 응답 객체로 변환
        List<PortfolioResponse> responses = top4Portfolios.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        // 응답 객체를 ResponseEntity에 담아 반환
        return ResponseEntity.ok(responses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> update(@PathVariable Long id, @RequestBody PortfolioRequest.Update portfolio) {
        Optional<PortfolioEntity> portfolioEntityOptional = portfolioRepository.findById(id);
        if (portfolioEntityOptional.isPresent()) {
            PortfolioEntity portfolioEntity = portfolioEntityOptional.get();
            portfolioEntity.setCareer(portfolio.getCareer());
            portfolioEntity.setPosition(portfolio.getPosition());
            portfolioEntity.setContent(portfolio.getContent());
            portfolioEntity.setProfile(portfolio.getProfile());
            portfolioRepository.save(portfolioEntity);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        Optional<PortfolioEntity> portfolioEntityOptional = portfolioRepository.findById(id);
        if (portfolioEntityOptional.isPresent()) {
            portfolioRepository.deleteById(id);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private PortfolioResponse toResponse(PortfolioEntity entity) {
        return new PortfolioResponse(
                entity.getId(),
                entity.getCreatedAt().toString(),
                entity.getPosition(),
                entity.getCareer(),
                entity.getCount(),
                entity.getProfile(),
                entity.getContent(),
                entity.getUser().getId()
        );
    }

}
