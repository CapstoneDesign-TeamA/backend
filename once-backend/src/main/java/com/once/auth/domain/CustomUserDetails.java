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
        return user.getEmail(); // 이메일을 username으로 사용
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한은 필요 없으므로 빈 리스트
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