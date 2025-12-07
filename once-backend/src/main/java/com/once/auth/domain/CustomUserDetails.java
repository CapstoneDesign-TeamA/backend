/**
 * File: CustomUserDetails.java
 * Description:
 *  - Spring Security UserDetails 구현체
 *  - User 엔티티 기반 인증 정보 제공
 *  - 이메일을 username으로 사용
 */

package com.once.auth.domain;

import com.once.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private final User user;

    // 기존 User 객체 기반 생성자
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ID, email, password 기반 임시 User 생성용 생성자
    public CustomUserDetails(Long id, String email, String password) {
        User temp = new User();
        temp.setId(id);
        temp.setEmail(email);
        temp.setPassword(password);
        this.user = temp;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}