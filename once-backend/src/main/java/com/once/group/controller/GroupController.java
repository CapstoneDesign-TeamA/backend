package com.once.group.controller;

import com.once.group.dto.*;
import com.once.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // 그룹 생성 API
    @PostMapping("/create")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupCreateRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<GroupResponse>> getMyGroups() {
        List<GroupResponse> response = groupService.getMyGroups();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long id,
            @RequestBody GroupCreateRequest request) {
        GroupResponse updatedGroup = groupService.updateGroup(id, request);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok("그룹이 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<GroupInviteResponse> inviteMember(
            @PathVariable Long groupId,
            @RequestBody GroupInviteRequest request) {

        // URL의 groupId를 DTO에 설정
        request.setGroupId(groupId);

        GroupInviteResponse response = groupService.inviteMember(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/schedules")
    public ResponseEntity<ScheduleResponse> createSchedule(
            @PathVariable Long groupId,
            @RequestBody ScheduleCreateRequest request) {
        ScheduleResponse response = groupService.createSchedule(groupId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByGroup(@PathVariable Long groupId) {
        List<ScheduleResponse> schedules = groupService.getSchedulesByGroup(groupId);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleCreateRequest request) {
        ScheduleResponse response = groupService.updateSchedule(groupId, scheduleId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<Map<String, String>> deleteSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId) {
        groupService.deleteSchedule(groupId, scheduleId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "일정이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
