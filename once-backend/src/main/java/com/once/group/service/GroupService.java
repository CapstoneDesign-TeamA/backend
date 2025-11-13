package com.once.group.service;

import com.once.group.domain.Group;
import com.once.group.domain.Schedule;
import com.once.group.dto.*;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
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

    // 그룹 생성
    public GroupResponse createGroup(String name, String description, String imageUrl) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setImageUrl(imageUrl);

        Group saved = groupRepository.save(group);
        return toResponse(saved); // 공통 매퍼 사용
    }

    // 내 그룹 조회  임시용
    public List<GroupResponse> getMyGroups() {
        return groupRepository.findAll()
                .stream()
                .map(this::toResponse) // 공통 매퍼 사용
                .collect(Collectors.toList());
    }

    // 내 그룹 조회
    public List<GroupSummaryResponse> getMyGroupsSummary() {
        List<Group> groups = groupRepository.findAll(); // 실제로는 사용자 기준 필터 필요

        return groups.stream().map(group -> {
            GroupSummaryResponse dto = new GroupSummaryResponse();
            dto.setGroupId(group.getId());
            dto.setName(group.getName());
            dto.setMemberCount(5); // 임시값, 이후 Member 테이블 연동 시 변경
            dto.setLastActive("2025-09-26"); // 예시, 추후 변경
            return dto;
        }).collect(Collectors.toList());
    }

    // 그룹 상세 보기  임시용
    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. ID: " + groupId));
        return toResponse(group); // 공통 매퍼 사용
    }

    // 그룹 상세 보기
    public GroupDetailResponse getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        GroupDetailResponse dto = new GroupDetailResponse();
        dto.setGroupId(group.getId());
        dto.setName(group.getName());

        // 구성원 (추후 Member 테이블 연동 예정)
        dto.setMembers(List.of("A", "B", "C"));

        // 일정 (이미 ScheduleRepository 있음)
        List<ScheduleResponse> schedules = scheduleRepository.findByGroupId(groupId)
                .stream().map(schedule -> {
                    ScheduleResponse s = new ScheduleResponse();
                    s.setScheduleId(schedule.getId());
                    s.setTitle(schedule.getTitle());
                    s.setDate(schedule.getDate().toString());
                    s.setTime(schedule.getTime().toString());
                    s.setDescription(schedule.getDescription());
                    s.setCreatedAt(schedule.getCreatedAt());
                    return s;
                }).collect(Collectors.toList());
        dto.setSchedules(schedules);

        // 앨범 목록 (현재 미구현이므로 임시 데이터)
        dto.setAlbums(List.of("https://example.com/album1.jpg", "https://example.com/album2.jpg"));

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

    // 그룹 삭제
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("존재하지 않는 그룹입니다.");
        }
        groupRepository.deleteById(id);
    }

    // 공통: 엔티티 -> 응답 DTO 매핑 (setter 방식으로 통일)
    private GroupResponse toResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setImageUrl(group.getImageUrl());
        response.setCreatedAt(group.getCreatedAt());
        return response;
    }

    public GroupInviteResponse inviteMember(GroupInviteRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        // 실제 이메일 전송 / 회원 등록 로직은 생략
        // 현재는 단순히 초대 요청을 처리했다고 가정
        return new GroupInviteResponse("초대가 완료되었습니다.", group.getId(), request.getEmail());
    }

    // 일정 등록
    public ScheduleResponse createSchedule(Long groupId, ScheduleCreateRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Schedule schedule = new Schedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDate(LocalDate.parse(request.getDate()));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        schedule.setTime(LocalTime.parse(request.getTime(), timeFormatter));
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
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

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

        // 해당 일정이 해당 그룹에 속하는지 검증
        if (!schedule.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        // 변경 가능 필드 업데이트
        if (request.getTitle() != null) schedule.setTitle(request.getTitle());
        if (request.getDate() != null) schedule.setDate(LocalDate.parse(request.getDate()));
        if (request.getTime() != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            schedule.setTime(LocalTime.parse(request.getTime(), timeFormatter));
        }
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
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 일정입니다."));

        if (!schedule.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        scheduleRepository.delete(schedule);
    }

}
