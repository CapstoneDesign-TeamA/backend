/**
 * File: AlbumService.java
 * Description:
 *  - 그룹 앨범 생성, 조회, 수정, 삭제를 처리하는 서비스
 *  - 이미지 분석 결과를 기반으로 그룹 활동 카테고리를 기록
 *  - 이미지 변경 시 AI 분석을 다시 수행하여 활동 로그를 남김
 */

package com.once.group.service;

import com.once.ai.service.AiService;
import com.once.group.domain.Album;
import com.once.group.domain.Group;
import com.once.group.dto.AlbumResponse;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;
    private final AiService aiService;
    private final GroupActivityCategoryService groupActivityCategoryService;

    @Transactional
    public AlbumResponse createAlbum(Long groupId, Long userId, String title, String description, String imageUrl) {

        // 그룹 조회 후 유효성 검사
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        // 이미지 분석 실행
        String aiCategory = analyzeCategory(imageUrl);

        // 분석 결과가 존재하면 그룹 활동 기록에 추가
        if (aiCategory != null) {
            groupActivityCategoryService.recordCategory(groupId, userId, aiCategory);
        }

        // 앨범 엔티티 생성 후 저장
        Album album = Album.builder()
                .group(group)
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        Album saved = albumRepository.save(album);
        return toResponse(saved);
    }

    public List<AlbumResponse> getAlbumsByGroup(Long groupId) {
        // 그룹 ID 기준으로 앨범 목록 조회 후 DTO로 변환
        return albumRepository.findByGroupId(groupId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlbumResponse updateAlbum(Long groupId, Long albumId, Long userId,
                                     String title, String description, String imageUrl) {

        // 앨범 조회 및 유효성 검사
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        // 요청된 그룹과 앨범이 속한 그룹 일치 여부 검사
        if (!album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        // 제목 및 설명 수정
        if (title != null) album.setTitle(title);
        if (description != null) album.setDescription(description);

        // 이미지 변경이 있는 경우에만 AI 분석 및 업데이트 수행
        if (imageUrl != null && !imageUrl.equals(album.getImageUrl())) {
            String aiCategory = analyzeCategory(imageUrl);

            // 분석 결과가 있으면 활동 카테고리 기록
            if (aiCategory != null) {
                groupActivityCategoryService.recordCategory(groupId, userId, aiCategory);
            }

            album.setImageUrl(imageUrl);
        }

        Album updated = albumRepository.save(album);
        return toResponse(updated);
    }

    @Transactional
    public void deleteAlbum(Long groupId, Long albumId) {

        // 앨범 조회 후 그룹 일치 여부 확인
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (!album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        // 앨범 삭제
        albumRepository.delete(album);
    }

    private String analyzeCategory(String imageUrl) {
        // 이미지 분석 후 category 필드 추출
        try {
            Map<String, Object> resp = aiService.analyzeImageUrl(Map.of("image_url", imageUrl));
            return resp != null ? (String) resp.get("category") : null;
        } catch (Exception e) {
            return null;
        }
    }

    private AlbumResponse toResponse(Album album) {
        // Album 엔티티를 AlbumResponse DTO로 변환
        AlbumResponse response = new AlbumResponse();
        response.setAlbumId(album.getId());
        response.setGroupId(album.getGroup().getId());
        response.setTitle(album.getTitle());
        response.setDescription(album.getDescription());
        response.setImageUrl(album.getImageUrl());
        response.setCreatedAt(album.getCreatedAt());
        return response;
    }

    @Transactional
    public void deleteAlbumByImageUrl(Long groupId, String imageUrl) {
        // 그룹 ID + 이미지 URL 기반으로 앨범 조회
        Album album = albumRepository
                .findByGroupIdAndImageUrl(groupId, imageUrl)
                .orElseThrow(() -> new RuntimeException("해당 이미지 URL의 앨범을 찾을 수 없습니다."));

        // 앨범 삭제
        albumRepository.delete(album);
    }
}