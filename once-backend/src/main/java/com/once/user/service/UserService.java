/**
 * UserService
 * 사용자 생성, 프로필 관리, 관심사/약관 저장, 활동 로그 기록 기능을 처리하는 서비스 클래스
 * 비즈니스 로직은 유지하고 주석 스타일만 통일함
 */

package com.once.user.service;

import ch.qos.logback.classic.Logger;
import com.once.config.ClientInfoUtil;
import com.once.auth.dto.SignupRequest;
import com.once.user.dto.UserProfileUpdateRequest;
import com.once.user.dto.UserProfileUpdateResponse;
import com.once.user.dto.UserResponse;
import com.once.user.mapper.UserMapper;
import com.once.user.domain.TermsAgreement;
import com.once.user.domain.User;
import com.once.user.domain.UserActivityLog;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 이메일 존재 여부
    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email).isPresent();
    }

    // 아이디 존재 여부
    public boolean isUsernameExists(String username) {
        return userMapper.findByUsername(username).isPresent();
    }

    // 닉네임 존재 여부
    public boolean isNicknameExists(String nickname) {
        return userMapper.findByNickname(nickname).isPresent();
    }

    // 사용자 조회
    public Optional<User> findById(Long id) {
        return userMapper.findById(id);
    }

    // 회원가입 처리
    @Transactional
    public User createUser(SignupRequest signupRequest) {
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

        // 사용자 기본 정보 생성
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setNickname(signupRequest.getNickname());

        logger.info("회원가입 - 사용자 생성 시작: username={}, email={}, nickname={}",
                signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getNickname());

        userMapper.insertUser(user);
        logger.info("회원가입 - 사용자 DB 저장 완료: userId={}", user.getId());

        // 관심사 저장
        logger.info("회원가입 - 관심사 개수: {}",
                signupRequest.getInterests() != null ? signupRequest.getInterests().size() : 0);

        if (signupRequest.getInterests() != null && !signupRequest.getInterests().isEmpty()) {
            logger.info("회원가입 - 관심사 목록: {}", signupRequest.getInterests());
            int savedCount = 0;

            for (String interest : signupRequest.getInterests()) {
                if (interest != null && !interest.trim().isEmpty()) {
                    logger.info("회원가입 - 관심사 저장 중: userId={}, interest={}", user.getId(), interest.trim());
                    userMapper.insertUserInterest(user.getId(), interest.trim());
                    savedCount++;
                }
            }

            logger.info("회원가입 - 관심사 저장 완료: userId={}, 저장된 개수={}", user.getId(), savedCount);

        } else {
            logger.warn("회원가입 - 관심사가 비어있음: userId={}", user.getId());
        }

        // 약관 동의 저장
        saveTermsAgreements(user.getId(), signupRequest.getMarketingAgreed());
        logger.info("회원가입 - 약관 동의 저장 완료: userId={}", user.getId());

        return user;
    }

    // 약관 저장 처리
    private void saveTermsAgreements(Long userId, Boolean marketingAgreed) {

        TermsAgreement serviceTerms = new TermsAgreement();
        serviceTerms.setUserId(userId);
        serviceTerms.setTermType("SERVICE_TERMS");
        serviceTerms.setAgreed(true);
        serviceTerms.setAgreedVersion("1.0");
        serviceTerms.setAgreedAt(LocalDateTime.now());
        userMapper.insertTermsAgreement(serviceTerms);

        TermsAgreement privacyPolicy = new TermsAgreement();
        privacyPolicy.setUserId(userId);
        privacyPolicy.setTermType("PRIVACY_POLICY");
        privacyPolicy.setAgreed(true);
        privacyPolicy.setAgreedVersion("1.0");
        privacyPolicy.setAgreedAt(LocalDateTime.now());
        userMapper.insertTermsAgreement(privacyPolicy);

        if (marketingAgreed != null && marketingAgreed) {
            TermsAgreement marketing = new TermsAgreement();
            marketing.setUserId(userId);
            marketing.setTermType("MARKETING");
            marketing.setAgreed(true);
            marketing.setAgreedVersion("1.0");
            marketing.setAgreedAt(LocalDateTime.now());
            userMapper.insertTermsAgreement(marketing);
        }
    }

    // 이메일 기반 사용자 조회
    public User findByEmail(String email) {
        return userMapper.findByEmail(email).orElse(null);
    }

    // 사용자 비활성화
    @Transactional
    public void deactivateUser(Long userId) {
        userMapper.deactivateUser(userId);
    }

    // 프로필 단일 조회
    public UserResponse getUserById(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }

    // 프로필 업데이트
    @Transactional
    public UserProfileUpdateResponse updateProfile(
            Long userId,
            UserProfileUpdateRequest request
    ) {

        User user = userMapper.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateUserProfile(user);

        return UserProfileUpdateResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .message("프로필이 업데이트되었습니다")
                .build();
    }

    // 활동 로그 기록
    @Transactional
    public void logUserActivity(Long user_id, String activity_type, String description) {
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

        try {
            String ipAddress = ClientInfoUtil.getClientIpAddress();
            String userAgent = ClientInfoUtil.getClientUserAgent();

            UserActivityLog log = new UserActivityLog();
            log.setUserId(user_id);
            log.setActivityType(activity_type);
            log.setDescription(description);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setCreatedAt(LocalDateTime.now());

            userMapper.insertUserActivityLog(log);
            logger.info("활동 로그 기록 완료: 사용자 ID={}, 활동 유형={}", user_id, activity_type);

        } catch (Exception e) {
            logger.error("활동 로그 기록 실패: {}", e.getMessage());
        }
    }
}