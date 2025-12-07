/**
 * File: AlbumRepository.java
 * Description:
 *  - 앨범 엔티티용 JPA 리포지토리
 *  - 그룹별 앨범 조회, 이미지 URL 기반 조회/삭제, 모임 기반 후기 앨범 조회 등 제공
 */

package com.once.group.repository;

import com.once.group.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByGroupIdAndMeetingId(Long groupId, Long meetingId); // 모임-앨범 매핑 조회

    List<Album> findByGroupId(Long groupId); // 그룹 전체 앨범 조회

    List<Album> findByImageUrlIn(List<String> imageUrls); // 이미지 URL 목록으로 조회

    void deleteByImageUrlIn(List<String> imageUrls); // 이미지 URL 목록으로 일괄 삭제

    Optional<Album> findByGroupIdAndImageUrl(Long groupId, String imageUrl); // 단일 이미지 URL 조회
}