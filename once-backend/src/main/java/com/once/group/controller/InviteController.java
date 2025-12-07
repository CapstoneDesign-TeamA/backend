/**
 * File: InviteController.java
 * Description:
 *  - 그룹 초대 링크 생성, 검증, 수락을 처리하는 컨트롤러
 *  - 초대 토큰 기반 그룹 가입 흐름 관리
 */

package com.once.group.controller;

import com.once.auth.domain.CustomUserDetails;
import com.once.group.domain.InviteToken;
import com.once.group.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;


    /**
     * 초대 링크 생성
     * GET /invite/create/{groupId}
     * - groupId 기반 초대 토큰 생성
     * - 클라이언트는 반환된 링크를 직접 사용자에게 전달하거나 공유
     */
    @PostMapping("/create/{groupId}")
    public Object createInvite(@PathVariable Long groupId) {
        String token = inviteService.createInviteLink(groupId);

        return new Object() {
            public final String inviteLink = "https://once.com/invite/" + token;
        };
    }


    /**
     * 초대 토큰 검증
     * GET /invite/validate?token=xxx
     * - 해당 token이 유효한지 체크
     * - 유효한 경우 groupId 반환
     */
    @GetMapping("/validate")
    public Object validate(@RequestParam String token) {
        InviteToken inviteToken = inviteService.validateToken(token);

        return new Object() {
            public final Long groupId = inviteToken.getGroupId();
            public final boolean valid = true;
        };
    }


    /**
     * 초대 수락
     * POST /invite/accept?token=xxx
     * - 유효 토큰인지 확인 후 해당 그룹에 사용자 추가
     * - 로그인 필요
     */
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