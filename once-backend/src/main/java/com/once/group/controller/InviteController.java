package com.once.group.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.group.domain.InviteToken;
import com.once.group.service.InviteService;
import com.once.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    // 1) 초대 링크 생성
    @PostMapping("/create/{groupId}")
    public Object createInvite(@PathVariable Long groupId) {
        String token = inviteService.createInviteLink(groupId);

        return new Object() {
            public final String inviteLink = "https://once.com/invite/" + token;
        };
    }

    // 2) 초대 토큰 검증
    @GetMapping("/validate")
    public Object validate(@RequestParam String token) {
        InviteToken inviteToken = inviteService.validateToken(token);

        return new Object() {
            public final Long groupId = inviteToken.getGroupId();
            public final boolean valid = true;
        };
    }

    // 3) 초대 수락
    @PostMapping("/accept")
    public Object accept(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        inviteService.acceptInvite(token, user.getUser());

        return new Object() {
            public final String message = "그룹에 성공적으로 참여했습니다.";
        };
    }
}