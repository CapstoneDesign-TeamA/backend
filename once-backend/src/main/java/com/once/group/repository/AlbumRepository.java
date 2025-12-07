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

    // ✅ 이미지 URL 목록으로 앨범 조회 (피드 삭제 시 사용)
    List<Album> findByImageUrlIn(List<String> imageUrls);

    // ✅ 이미지 URL 목록으로 앨범 일괄 삭제 (피드 삭제 시 사용)
    void deleteByImageUrlIn(List<String> imageUrls);

    // 그룹 ID와 이미지 URL로 앨범 조회
    Optional<Album> findByGroupIdAndImageUrl(Long groupId, String imageUrl);

    

}