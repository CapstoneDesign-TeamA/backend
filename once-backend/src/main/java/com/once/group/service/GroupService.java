package com.once.group.service;

import com.once.auth.util.SecurityUtil;
import com.once.group.domain.*;
import com.once.group.dto.*;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupMemberRepository;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.ScheduleRepository;
import com.once.user.domain.User;
import com.once.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;
    private final AlbumRepository albumRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;  // ★ 추가됨

    // 그룹 생성
    public GroupResponse createGroup(String name, String description, String imageUrl) {

        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("로그인된 사용자 정보를 찾을 수 없습니다.");
        }

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setImageUrl(imageUrl);

        Group saved = groupRepository.save(group);

        GroupMember leader = new GroupMember();
        leader.setGroup(saved);
        leader.setUserId(userId);
        leader.setRole(GroupRole.LEADER);

        groupMemberRepository.save(leader);

        return toResponse(saved);
    }

    // 내 그룹 목록 조회
    public List<GroupResponse> getMyGroups() {
        Long userId = SecurityUtil.getCurrentUserId();

        // 실제로는 userId 기준으로 필터해야 함
        List<GroupMember> myMembership = groupMemberRepository.findByUserId(userId);

        return myMembership.stream()
                .map(gm -> toResponse(gm.getGroup()))
                .collect(Collectors.toList());
    }

    // 사용자 기준 그룹 요약 조회
    public List<GroupSummaryResponse> getMyGroupsSummary() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<GroupMember> members = groupMemberRepository.findByUserId(userId);

        return members.stream()
                .map(m -> {
                    Group g = m.getGroup();
                    GroupSummaryResponse dto = new GroupSummaryResponse();
                    dto.setGroupId(g.getId());
                    dto.setName(g.getName());
                    dto.setMemberCount((int) groupMemberRepository.countByGroupId(g.getId()));
                    dto.setLastActive("2025-09-26"); // TODO: 추후 실제 활동 기반 계산
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 그룹 상세 조회 (전체)
    public GroupDetailResponse getGroupDetail(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        GroupDetailResponse dto = new GroupDetailResponse();
        dto.setGroupId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setImageUrl(group.getImageUrl());

        // ★ 실제 구성원 로드
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

        List<String> memberNames = members.stream()
                .map(member ->
                        userRepository.findById(member.getUserId())
                                .map(User::getNickname)  // 닉네임
                                .orElse("Unknown-" + member.getUserId())
                )
                .toList();

        dto.setMembers(memberNames);

        // 일정 목록
        List<ScheduleResponse> schedules = scheduleRepository.findByGroupId(groupId)
                .stream()
                .map(schedule -> {
                    ScheduleResponse s = new ScheduleResponse();
                    s.setScheduleId(schedule.getId());
                    s.setGroupId(groupId);
                    s.setTitle(schedule.getTitle());
                    s.setDate(schedule.getDate().toString());
                    s.setTime(schedule.getTime().toString());
                    s.setDescription(schedule.getDescription());
                    s.setCreatedAt(schedule.getCreatedAt());
                    return s;
                })
                .collect(Collectors.toList());

        dto.setSchedules(schedules);

        // 앨범 목록
        List<String> albumUrls = albumRepository.findByGroupId(groupId)
                .stream()
                .map(Album::getImageUrl)
                .collect(Collectors.toList());

        dto.setAlbums(albumUrls);

        return dto;
    }

    // 그룹 수정
    public GroupResponse updateGroup(Long id, String name, String description, String imageUrl) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        if (name != null) group.setName(name);
        if (description != null) group.setDescription(description);
        if (imageUrl != null) group.setImageUrl(imageUrl);

        Group updated = groupRepository.save(group);
        return toResponse(updated);
    }

    // 그룹 나가기
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {

        GroupMember member = groupMemberRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("그룹에 속해있지 않음"));

        if (member.getRole() == GroupRole.LEADER) {
            throw new IllegalArgumentException("그룹장은 그룹에서 나갈 수 없습니다.");
        }

        groupMemberRepository.delete(member);

        if (groupMemberRepository.findByGroupId(groupId).isEmpty()) {
            groupRepository.deleteById(groupId);
        }
    }

    // 그룹 삭제
    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        GroupMember leader = groupMemberRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("그룹에 속해있지 않음"));

        if (leader.getRole() != GroupRole.LEADER) {
            throw new IllegalArgumentException("그룹 삭제 권한이 없습니다.");
        }

        groupMemberRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    // 공통 mapper
    private GroupResponse toResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setImageUrl(group.getImageUrl());
        response.setCreatedAt(group.getCreatedAt());
        return response;
    }

    // 멤버 초대 (추후 실제 초대 로직 필요)
    public GroupInviteResponse inviteMember(GroupInviteRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        return new GroupInviteResponse("초대가 완료되었습니다.", group.getId(), request.getEmail());
    }

    // 일정 생성
    public ScheduleResponse createSchedule(Long groupId, ScheduleCreateRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDate(LocalDate.parse(request.getDate()));
        schedule.setTime(LocalTime.parse(request.getTime(), DateTimeFormatter.ofPattern("HH:mm")));
        schedule.setDescription(request.getDescription());

        Schedule saved = scheduleRepository.save(schedule);

        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleId(saved.getId());
        response.setGroupId(groupId);
        response.setTitle(saved.getTitle());
        response.setDate(saved.getDate().toString());
        response.setTime(saved.getTime().toString());
        response.setDescription(saved.getDescription());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    // 일정 조회
    public List<ScheduleResponse> getSchedulesByGroup(Long groupId) {
        List<Schedule> schedules = scheduleRepository.findByGroupId(groupId);

        return schedules.stream().map(schedule -> {
            ScheduleResponse response = new ScheduleResponse();
            response.setScheduleId(schedule.getId());
            response.setGroupId(groupId);
            response.setTitle(schedule.getTitle());
            response.setDate(schedule.getDate().toString());
            response.setTime(schedule.getTime().toString());
            response.setDescription(schedule.getDescription());
            response.setCreatedAt(schedule.getCreatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    // 일정 수정
    public ScheduleResponse updateSchedule(Long groupId, Long scheduleId, ScheduleCreateRequest request) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 일정입니다."));

        if (!schedule.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        if (request.getTitle() != null) schedule.setTitle(request.getTitle());
        if (request.getDate() != null) schedule.setDate(LocalDate.parse(request.getDate()));
        if (request.getTime() != null)
            schedule.setTime(LocalTime.parse(request.getTime(), DateTimeFormatter.ofPattern("HH:mm")));
        if (request.getDescription() != null) schedule.setDescription(request.getDescription());

        Schedule updated = scheduleRepository.save(schedule);

        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleId(updated.getId());
        response.setGroupId(groupId);
        response.setTitle(updated.getTitle());
        response.setDate(updated.getDate().toString());
        response.setTime(updated.getTime().toString());
        response.setDescription(updated.getDescription());
        response.setCreatedAt(updated.getCreatedAt());

        return response;
    }

    // 일정 삭제
    public void deleteSchedule(Long groupId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 일정입니다."));

        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}