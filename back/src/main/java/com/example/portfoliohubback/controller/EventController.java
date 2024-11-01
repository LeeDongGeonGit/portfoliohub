package com.example.portfoliohubback.controller;

import com.example.portfoliohubback.controller.request.EventRequest;
import com.example.portfoliohubback.controller.response.ApiResponse;
import com.example.portfoliohubback.controller.response.ResponseCode;
import com.example.portfoliohubback.controller.response.EventResponse;
import com.example.portfoliohubback.entity.EventEntity;
import com.example.portfoliohubback.entity.UserEntity;
import com.example.portfoliohubback.repository.EventRepository;
import com.example.portfoliohubback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j // -> 이건 로그를 쉽게 찍기 위한 어노테이션임. 검색해보시길 바람
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    // JWT 토큰에서 사용자 ID를 추출하는 메서드
    private String getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }
    // 등록
    @PostMapping
    public ResponseEntity<ApiResponse<EventEntity>> createEvent(@RequestBody EventRequest.Create event,
                                                                @RequestHeader("Authorization") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.response(false, ResponseCode.BAD_REQUEST, null));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uId = null;
        if (authentication != null) {
            uId = authentication.getName();
        }

        UserEntity user = userRepository.findById(uId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        EventEntity newEvent = new EventEntity();
        newEvent.setTitle(event.getTitle());
        newEvent.setContent(event.getContent());
        newEvent.setStartTime(event.getStartTime());
        newEvent.setEndTime(event.getEndTime());
        newEvent.setUser(user);

        EventEntity savedEvent = eventRepository.save(newEvent);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.response(true, ResponseCode.Created, savedEvent));
    }

    // 이 달에 대해서 일정 다 가져오기
    @GetMapping
    public ApiResponse<List<EventResponse.eventOne>> getEventsByMonth(@RequestParam("month") int month) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            System.out.println("아이디 받아온 값 : "+userId);
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 해당 월의 이벤트를 가져오기 위해 Repository를 사용
        List<EventEntity> events = eventRepository.findByUserIdAndMonth(userId, month);

        // 이벤트를 응답용 DTO로 변환
        List<EventResponse.eventOne> eventResponses = events.stream()
                .map(EventResponse.eventOne::of)
                .collect(Collectors.toList());

        // 변환된 DTO를 ApiResponse로 감싸서 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, eventResponses);
    }


    // 선택한 하루에 대해서 일정 리스트 가져오기
    @GetMapping("/day")
    public ApiResponse<List<EventResponse.eventOne>> getEventsByDay(@RequestParam("month") int month, @RequestParam("day") int day) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            System.out.println("아이디 받아온 값 : "+userId);
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 해당 월과 일에 대한 이벤트를 가져오기 위해 Repository를 사용
        List<EventEntity> events = eventRepository.findByUserIdAndMonthAndDay(userId, month, day);

        // 이벤트를 응답용 DTO로 변환
        List<EventResponse.eventOne> eventResponses = events.stream()
                .map(EventResponse.eventOne::of)
                .collect(Collectors.toList());

        // 변환된 DTO를 ApiResponse로 감싸서 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, eventResponses);
    }

    // 한개 정보 상세보기
    @GetMapping("/{eventId}")
    public ApiResponse<EventResponse.eventOne> getEventsByDay(@PathVariable("eventId") Long eventId) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            System.out.println("아이디 받아온 값 : " + userId);
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 해당 eventId를 가진 이벤트를 가져오기 위해 Repository를 사용
        Optional<EventEntity> eventOptional = eventRepository.findById(eventId);

        if (!eventOptional.isPresent()) {
            // eventId에 해당하는 이벤트가 없는 경우
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 이벤트를 응답용 DTO로 변환
        EventEntity event = eventOptional.get();
        EventResponse.eventOne eventResponseOne = EventResponse.eventOne.of(event);

        // 변환된 DTO를 ApiResponse로 감싸서 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, eventResponseOne);
    }

    //이벤트 삭제
    @DeleteMapping("/{eventId}")
    public ApiResponse<String> deleteEvent(@PathVariable("eventId") Long eventId) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            System.out.println("아이디 받아온 값 : " + userId);
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 해당 eventId를 가진 이벤트를 가져오기 위해 Repository를 사용
        Optional<EventEntity> eventOptional = eventRepository.findById(eventId);

        if (!eventOptional.isPresent()) {
            // eventId에 해당하는 이벤트가 없는 경우
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        // 이벤트가 해당 사용자의 것인지 확인
        EventEntity event = eventOptional.get();
        if (!event.getUser().getId().equals(userId)) {
            // 이벤트의 소유자가 다른 경우
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        // 이벤트 삭제
        eventRepository.delete(event);

        // 삭제 성공 메시지 반환
        return ApiResponse.response(true, ResponseCode.SUCCESS, "삭제를 완료했습니다.");
    }

    @PutMapping("/{eventId}")
    public ApiResponse<EventResponse.eventOne> updateEvent(@PathVariable("eventId") Long eventId,
                                                           @RequestBody EventRequest.Update eventUpdate) {
        String userId = getUserIdFromToken();
        if (userId == null) {
            return ApiResponse.response(false, ResponseCode.UNAUTHORIZED, null);
        }

        Optional<EventEntity> eventOptional = eventRepository.findById(eventId);

        if (!eventOptional.isPresent()) {
            return ApiResponse.response(false, ResponseCode.NOT_FOUND, null);
        }

        EventEntity event = eventOptional.get();
        // 권환 있는지 보는거임
        if (!event.getUser().getId().equals(userId)) {
            return ApiResponse.response(false, ResponseCode.FORBIDDEN, null);
        }

        event.setTitle(eventUpdate.getTitle());
        event.setContent(eventUpdate.getContent());
        event.setStartTime(eventUpdate.getStartTime());
        event.setEndTime(eventUpdate.getEndTime());

        EventEntity updatedEvent = eventRepository.save(event);
        EventResponse.eventOne eventResponseOne = EventResponse.eventOne.of(updatedEvent);
        return ApiResponse.response(true, ResponseCode.SUCCESS, eventResponseOne);
    }
}