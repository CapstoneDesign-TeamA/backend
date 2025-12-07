/**
 * File: AutoAlbumService.java
 * Description:
 *  - 모임 종료 시 자동으로 앨범을 생성하는 서비스
 *  - 모임 완료 여부 검증 후 앨범을 생성하여 저장
 *  - 자동 생성된 앨범 정보를 DTO 형태로 반환
 */

package com.once.group.service;

import com.once.group.domain.Album;
import com.once.group.domain.Group;
import com.once.group.domain.Schedule;
import com.once.group.dto.AlbumResponse;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupRepository;
import com.once.group.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AutoAlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;
    private final ScheduleRepository scheduleRepository;

    public AlbumResponse createAutoAlbum(Long groupId, Long meetingId) {

        // 모임 일정 조회 후 유효성 검사
        Schedule schedule = scheduleRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 모임입니다."));

        // 모임이 이미 종료되었는지 확인
        if (schedule.getDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("아직 완료되지 않은 모임입니다.");
        }

        // 모임이 속한 그룹이 요청된 그룹과 일치하는지 확인
        Group group = schedule.getGroup();
        if (group == null || !group.getId().equals(groupId)) {
            throw new EntityNotFoundException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        // 자동 생성 앨범 엔티티 구성
        Album album = new Album();
        album.setGroup(group);
        album.setTitle(schedule.getTitle() + " 모임 앨범");
        album.setDescription("모임이 완료되어 자동 생성된 앨범입니다.");
        album.setImageUrl("auto-generated"); // 기본 이미지 설정
        album.setCreatedAt(LocalDateTime.now());

        // 앨범 저장
        Album saved = albumRepository.save(album);

        // 응답 DTO 변환
        AlbumResponse response = new AlbumResponse();
        response.setAlbumId(saved.getId());
        response.setGroupId(groupId);
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setImageUrl(saved.getImageUrl());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }
}