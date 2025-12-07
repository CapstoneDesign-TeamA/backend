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

    // 1) 초대 링크 생성
    public String createInviteLink(Long groupId) {

        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        String token = UUID.randomUUID().toString().replace("-", "");

        InviteToken inviteToken = InviteToken.builder()
                .groupId(groupId)
                .token(token)
                .expiredAt(LocalDateTime.now().plusDays(2))
                .used(false)
                .build();

        inviteTokenRepository.save(inviteToken);
        return token;
    }

    // 2) 초대 토큰 검증
    public InviteToken validateToken(String token) {
        InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 초대 링크입니다."));

        // used 체크 제거 - 여러 명이 사용 가능하도록 변경
        // if (inviteToken.isUsed()) {
        //     throw new RuntimeException("이미 사용된 초대 링크입니다.");
        // }

        if (inviteToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("초대 링크가 만료되었습니다.");
        }

        return inviteToken;
    }

    // 3) 초대 수락
    public void acceptInvite(String token, User user) {

        InviteToken inviteToken = validateToken(token);

        Long groupId = inviteToken.getGroupId();

        // 그룹 존재 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        // 이미 가입된 사용자 여부 확인
        boolean already = groupMemberRepository
                .findByGroupIdAndUserId(groupId, user.getId())
                .isPresent();

        if (already) {
            throw new RuntimeException("이미 이 그룹에 참여중입니다.");
        }

        // 그룹 멤버 생성
        GroupMember gm = new GroupMember();
        gm.setGroup(group);
        gm.setUserId(user.getId());
        gm.setRole(GroupRole.MEMBER);         // 기본값 MEMBER
        gm.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(gm);

        // used 플래그 설정 제거 - 여러 명이 사용할 수 있도록 변경
        // inviteToken.setUsed(true);
        // inviteTokenRepository.save(inviteToken);
    }

    // 4) 초대 정보 조회
    public GroupInviteInfo getInviteInfo(String token) {
        // 토큰 검증
        InviteToken inviteToken = validateToken(token);
        Long groupId = inviteToken.getGroupId();

        // 그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        // 멤버 수 조회
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