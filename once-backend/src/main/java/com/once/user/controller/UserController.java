package com.once.user.controller;

import com.once.user.dto.InterestsUpdateRequest;
import com.once.user.dto.ProfileUpdateRequest;
import com.once.user.mapper.UserMapper;
import com.once.user.domain.TermsAgreement;
import com.once.user.domain.UserActivityLog;
import com.once.user.domain.UserInterest;
import com.once.auth.service.AuthService;
import jakarta.validation.Valid;
import com.once.auth.dto.SignupRequest;
import com.once.user.domain.User;
import com.once.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthService authService;
    //private final UserService userService;
    private final UserMapper userMapper;

    // 构造函数注入
    public UserController(AuthService authService, UserService userService, UserMapper userMapper) {
        this.authService = authService;
        //this.userService = userService;
        this.userMapper = userMapper;
    }

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        // 중복 체크
        if (userService.isEmailExists(signupRequest.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.isUsernameExists(signupRequest.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.isNicknameExists(signupRequest.getNickname())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이미 사용 중인 닉네임입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        // 사용자 생성
        User user = userService.createUser(signupRequest);

        // 활동 로그 기록
        userService.logUserActivity(user.getId(), "/signup", "사용자 생성");

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입 완료");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdrawUser(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 회원 탈퇴 처리
            userService.deactivateUser(user.getId());

            // 활동 로그 기록
            userService.logUserActivity(user.getId(), "/user/me", "회원 탈퇴");

            return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }



    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.isNicknameExists(nickname);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다.");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // JWT 토큰에서 이메일 추출
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 사용자 관심사 조회
            List<UserInterest> interests = userMapper.findInterestsByUserId(user.getId());
            List<String> interestList = interests.stream()
                    .map(UserInterest::getInterest)
                    .collect(Collectors.toList());

            // 프로필 데이터 구성
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("nickname", user.getNickname());
            profile.put("interests", interestList);
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(Map.of("success", true, "data", profile));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ProfileUpdateRequest request) {

        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 닉네임 중복 체크 (자신의 기존 닉네임 제외)
            if (!user.getNickname().equals(request.getNickname()) &&
                    userService.isNicknameExists(request.getNickname())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "이미 사용 중인 닉네임입니다."));
            }

            // 프로필 업데이트
            user.setNickname(request.getNickname());
            userMapper.updateUserProfile(user);

            // 관심사 업데이트
            userMapper.deleteUserInterests(user.getId());
            for (String interest : request.getInterests()) {
                userMapper.insertUserInterest(user.getId(), interest);
            }

            // 활동 로그 기록
            userService.logUserActivity(user.getId(), "/user.profile", "프로필 정보 수정");

            return ResponseEntity.ok(Map.of("success", true, "message", "프로필 수정 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    @GetMapping("/interests")
    public ResponseEntity<?> getUserInterests(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            List<UserInterest> interests = userMapper.findInterestsByUserId(user.getId());
            List<String> interestList = interests.stream()
                    .map(UserInterest::getInterest)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("interests", interestList)
            ));

        }catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }
    @PutMapping("/interests")
    public ResponseEntity<?> updateUserInterests(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody InterestsUpdateRequest request) {

        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 관심사 업데이트
            userMapper.deleteUserInterests(user.getId());
            for (String interest : request.getInterests())
            {
                userMapper.insertUserInterest(user.getId(), interest);
            }

            // 활동 로그 기록
            userService.logUserActivity(user.getId(), "/user/interests", "관심사 수정");

            return ResponseEntity.ok(Map.of("success", true, "message", "관심사 수정 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }
    @GetMapping("/terms")
    public ResponseEntity<?> getUserTermsAgreements(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            List<TermsAgreement> agreements = userMapper.findTermsAgreementsByUserId(user.getId());

            return ResponseEntity.ok(Map.of("success", true, "data", agreements));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }
    @GetMapping("/activity-logs")
    public ResponseEntity<?> getUserActivityLogs(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 페이징 계산
            int offset = page * size;

            // 활동 로그 조회
            List<UserActivityLog> logs = userMapper.findUserActivityLogs(user.getId(), offset, size);
            int totalCount = userMapper.countUserActivityLogs(user.getId());
            int totalPages = (int) Math.ceil((double) totalCount / size);

            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs);
            response.put("totalPages", totalPages);
            response.put("currentPage", page);
            response.put("totalCount", totalCount);
            response.put("pageSize", size);

            return ResponseEntity.ok(Map.of("success", true, "data", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

}