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

        Schedule schedule = scheduleRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 모임입니다."));

        // 완료된 모임인지 확인
        if (schedule.getDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("아직 완료되지 않은 모임입니다.");
        }

        Group group = schedule.getGroup();
        if (group == null || !group.getId().equals(groupId)) {
            throw new EntityNotFoundException("이 일정은 해당 그룹에 속하지 않습니다.");
        }

        // 앨범 생성
        Album album = new Album();
        album.setGroup(group);
        album.setTitle(schedule.getTitle() + " 모임 앨범");
        album.setDescription("모임이 완료되어 자동 생성된 앨범입니다.");
        album.setImageUrl("auto-generated"); // 임시로 설정함
        album.setCreatedAt(LocalDateTime.now());

        Album saved = albumRepository.save(album);

        // 응답 DTO 구성
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
