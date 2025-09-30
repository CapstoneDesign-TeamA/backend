package com.once.user.service;

import ch.qos.logback.classic.Logger;
import com.once.config.ClientInfoUtil;
import com.once.auth.dto.SignupRequest;
import com.once.user.mapper.UserMapper;
import com.once.user.domain.TermsAgreement;
import com.once.user.domain.User;
import com.once.user.domain.UserActivityLog;
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

    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email).isPresent();
    }

    public boolean isUsernameExists(String username) {
        return userMapper.findByUsername(username).isPresent();
    }

    public boolean isNicknameExists(String nickname) {
        return userMapper.findByNickname(nickname).isPresent();
    }
    public Optional<User> findById(Long id) {
        return userMapper.findById(id);
    }

    @Transactional
    public User createUser(SignupRequest signupRequest) {
        // User 생성
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setNickname(signupRequest.getNickname());

        userMapper.insertUser(user);

        // 관심사 저장
        for (String interest : signupRequest.getInterests()) {
            userMapper.insertUserInterest(user.getId(), interest);
        }

        // 약관 동의 저장
        saveTermsAgreements(user.getId(), signupRequest.getMarketingAgreed());

        return user;
    }

    private void saveTermsAgreements(Long userId, Boolean marketingAgreed) {
        // 필수 약관 동의
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

        // 선택 약관 동의 (마케팅)
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

    public User findByEmail(String email) {
        return userMapper.findByEmail(email).orElse(null);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        userMapper.deactivateUser(userId);
    }

    // 활동 로그 기록 메서드
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
            // 중요: 여기서 예외를 다시 던지지 않도록 주의
            // 트랜잭션이 롤백되는 것을 방지하기 위해
        }
    }
}