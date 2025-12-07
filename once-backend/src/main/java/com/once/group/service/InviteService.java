/**
 * File: InviteService.java
 * Description:
 *  - 그룹 초대 토큰 생성 및 검증 처리
 *  - 초대 링크 기반 그룹 가입 처리
 *  - 초대 링크 조회(미리보기) 기능 제공
 */

package com.once.group.service;

import com.once.group.domain.Group;
import com.once.group.domain.GroupMember;
import com.once.group.domain.GroupRole;
import com.once.group.domain.InviteToken;
import com.once.group.dto.GroupInviteInfo;
import com.once.group.repository.GroupMemberRepository;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.InviteTokenRepository;
import com.once.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteTokenRepository inviteTokenRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    // 초대 링크 생성
    public String createInviteLink(Long groupId) {

        // 그룹 존재 확인
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        // 랜덤 토큰 생성
        String token = UUID.randomUUID().toString().replace("-", "");

        // 초대 토큰 저장
        InviteToken inviteToken = InviteToken.builder()
                .groupId(groupId)
                .token(token)
                .expiredAt(LocalDateTime.now().plusDays(2))
                .used(false)
                .build();

        inviteTokenRepository.save(inviteToken);
        return token;
    }

    // 초대 토큰 검증
    public InviteToken validateToken(String token) {
        InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 초대 링크입니다."));

        // 여러 명 사용 가능하도록 used 체크 제거

        // 만료 여부 확인
        if (inviteToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("초대 링크가 만료되었습니다.");
        }

        return inviteToken;
    }

    // 초대 수락 처리
    public void acceptInvite(String token, User user) {

        // 토큰 유효성 확인
        InviteToken inviteToken = validateToken(token);
        Long groupId = inviteToken.getGroupId();

        // 그룹 존재 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        // 이미 가입된 유저인지 확인
        boolean already = groupMemberRepository
                .findByGroupIdAndUserId(groupId, user.getId())
                .isPresent();

        if (already) {
            throw new RuntimeException("이미 이 그룹에 참여중입니다.");
        }

        // 그룹 멤버 등록
        GroupMember gm = new GroupMember();
        gm.setGroup(group);
        gm.setUserId(user.getId());
        gm.setRole(GroupRole.MEMBER);
        gm.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(gm);

        // used 플래그 설정 제거 (여러 명 사용 가능)
    }

    // 초대 정보 조회
    public GroupInviteInfo getInviteInfo(String token) {

        // 토큰 검증 후 그룹 ID 획득
        InviteToken inviteToken = validateToken(token);
        Long groupId = inviteToken.getGroupId();

        // 그룹 정보 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        // 가입된 멤버 수 조회
        int membersCount = groupMemberRepository.findByGroupId(groupId).size();

        return GroupInviteInfo.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .imageUrl(group.getImageUrl())
                .membersCount(membersCount)
                .build();
    }
}