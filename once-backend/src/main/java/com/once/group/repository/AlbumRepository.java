package com.once.group.repository;

import com.once.group.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    // 후기 앨범 찾기
    Optional<Album> findByGroupIdAndMeetingId(Long groupId, Long meetingId);

    // 일반 피드 이미지 저장용 (groupId 기반)
    List<Album> findByGroupId(Long groupId);
}
