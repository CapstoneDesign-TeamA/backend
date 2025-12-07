// ================== 수정 완료된 전체 코드 ==================

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthService authService;
    private final UserMapper userMapper;

    @Autowired
    private UserService userService;

    public UserController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    // ================= 회원가입 =================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            if (userService.isEmailExists(signupRequest.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 이메일입니다."));
            }
            if (userService.isUsernameExists(signupRequest.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 아이디입니다."));
            }
            // 닉네임 중복 체크 제거 - 중복 허용
            // if (userService.isNicknameExists(signupRequest.getNickname())) {
            //     return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 닉네임입니다."));
            // }

            User user = userService.createUser(signupRequest);
            userService.logUserActivity(user.getId(), "/signup", "사용자 생성");

            return ResponseEntity.ok(Map.of("message", "회원가입 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ================= 회원 탈퇴 =================
    @DeleteMapping("/me")
    public ResponseEntity<?> withdrawUser(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            userService.deactivateUser(user.getId());
            userService.logUserActivity(user.getId(), "/users/me", "회원 탈퇴");

            return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    // ================= 내 프로필 조회 (/me) =================
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found", "message", "사용자를 찾을 수 없습니다."));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("name", user.getName() != null ? user.getName() : "");
            response.put("email", user.getEmail());
            response.put("profileImage", user.getProfileImage() != null ? user.getProfileImage() : "");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token", "message", "유효하지 않은 토큰입니다."));
        }
    }

    // ================= 닉네임/이메일/아이디 중복 체크 =================
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(Map.of(
                "exists", exists,
                "message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다."
        ));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        return ResponseEntity.ok(Map.of(
                "exists", exists,
                "message", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다."
        ));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.isNicknameExists(nickname);
        return ResponseEntity.ok(Map.of(
                "exists", exists,
                "message", exists ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다."
        ));
    }

    // ================= 전체 프로필 조회 (관심사 포함) =================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            logger.info("프로필 조회 요청 - 토큰: {}", token.substring(0, Math.min(50, token.length())));

            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            logger.info("토큰에서 추출한 이메일: {}", email);

            User user = userService.findByEmail(email);
            if (user == null) {
                logger.warn("사용자를 찾을 수 없음: email={}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            logger.info("사용자 조회 성공: userId={}, nickname={}", user.getId(), user.getNickname());

            List<String> interestList = userMapper.findInterestsByUserId(user.getId())
                    .stream()
                    .map(UserInterest::getInterest)
                    .toList();

            // HashMap 사용 (null 값 허용)
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("nickname", user.getNickname());
            profile.put("name", user.getName() != null ? user.getName() : "");
            profile.put("profileImage", user.getProfileImage() != null ? user.getProfileImage() : "");
            profile.put("interests", interestList);
            profile.put("createdAt", user.getCreatedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profile);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("프로필 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    // ================= 프로필 수정 =================
    @Transactional
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ProfileUpdateRequest request) {

        try {
            logger.info("프로필 업데이트 요청: nickname={}", request.getNickname());

            // 사용자 인증
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            if (user == null) {
                logger.warn("사용자를 찾을 수 없음: email={}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            // 닉네임 중복 체크 제거 (중복 허용)

            // 닉네임 업데이트
            user.setNickname(request.getNickname());

            // name 업데이트
            if (request.getName() != null) {
                user.setName(request.getName());
            }

            // profileImage 업데이트
            if (request.getProfileImage() != null) {
                user.setProfileImage(request.getProfileImage());
            }

            // DB 업데이트
            userMapper.updateUserProfile(user);

            // 관심사 업데이트
            userMapper.deleteUserInterests(user.getId());
            for (String interest : request.getInterests()) {
                userMapper.insertUserInterest(user.getId(), interest);
            }

            // 활동 로그
            userService.logUserActivity(user.getId(), "/users/profile", "프로필 정보 수정");

            logger.info("프로필 업데이트 성공: userId={}", user.getId());

            // 반환 JSON(HashMap 사용 — null 허용)
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "프로필 수정 완료");
            result.put("userId", user.getId());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            result.put("profileImage", user.getProfileImage() != null ? user.getProfileImage() : "");
            result.put("interests", request.getInterests());
            result.put("name", user.getName() != null ? user.getName() : "");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("프로필 업데이트 중 오류", e);

            // 인증 오류만 401
            if (e.getMessage() != null && e.getMessage().contains("JWT")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "인증에 실패했습니다."));
            }

            // 나머지는 서버 오류로 돌림
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "프로필 수정 중 오류가 발생했습니다."));
        }
    }

    // ================= 관심사 조회 =================
    @GetMapping("/interests")
    public ResponseEntity<?> getUserInterests(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.getUsernameFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "사용자를 찾을 수 없습니다."));
            }

            List<String> interestList = userMapper.findInterestsByUserId(user.getId())
                    .stream()
                    .map(UserInterest::getInterest)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("success", true, "data", Map.of("interests", interestList)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    // ================= 관심사 업데이트 =================
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

            userMapper.deleteUserInterests(user.getId());
            for (String interest : request.getInterests()) {
                userMapper.insertUserInterest(user.getId(), interest);
            }

            userService.logUserActivity(user.getId(), "/users/interests", "관심사 수정");

            return ResponseEntity.ok(Map.of("success", true, "message", "관심사 수정 완료"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "인증에 실패했습니다."));
        }
    }

    // ================= 활동 로그 조회 =================
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

            int offset = page * size;
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