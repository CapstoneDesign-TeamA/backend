package com.once.group.repository;

import com.once.group.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    // 후기 앨범 찾기
    Optional<Album> findByGroupIdAndMeetingId(Long groupId, Long meetingId);

    // 그룹별 앨범 전체 조회
    List<Album> findByGroupId(Long groupId);
}