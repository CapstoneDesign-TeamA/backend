package com.once.group.service;

import com.once.ai.service.AiService;
import com.once.group.domain.Album;
import com.once.group.domain.Group;
import com.once.group.dto.AlbumResponse;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;
    private final AiService aiService;
    private final GroupActivityCategoryService groupActivityCategoryService;

    // ============================================================
    // 앨범 생성 + AI 카테고리 → group_activity_category 저장
    // ============================================================
    public AlbumResponse createAlbum(Long groupId, String title, String description, String imageUrl) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        // AI 이미지 분석 → 그룹 활동 기록만 남김
        String aiCategory = analyzeCategory(imageUrl);
        if (aiCategory != null) {
            groupActivityCategoryService.recordCategory(groupId, 0L, aiCategory);
        }

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

    // ============================================================
    // 그룹별 앨범 목록 조회
    // ============================================================
    public List<AlbumResponse> getAlbumsByGroup(Long groupId) {
        return albumRepository.findByGroupId(groupId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // 앨범 수정 (이미지 변경 시에만 그룹 활동 카테고리 기록)
    // ============================================================
    public AlbumResponse updateAlbum(Long groupId, Long albumId,
                                     String title, String description, String imageUrl) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (!album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        if (title != null) album.setTitle(title);
        if (description != null) album.setDescription(description);

        // 이미지가 바뀐 경우에만 AI 재분석 + group_activity 기록
        if (imageUrl != null && !imageUrl.equals(album.getImageUrl())) {

            String aiCategory = analyzeCategory(imageUrl);

            if (aiCategory != null) {
                groupActivityCategoryService.recordCategory(groupId, 0L, aiCategory);
            }

            album.setImageUrl(imageUrl);
        }

        Album updated = albumRepository.save(album);
        return toResponse(updated);
    }

    // ============================================================
    // 앨범 삭제
    // ============================================================
    public void deleteAlbum(Long groupId, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (!album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        albumRepository.delete(album);
    }

    // ============================================================
    // AI 이미지 분석 (중복 제거)
    // ============================================================
    private String analyzeCategory(String imageUrl) {
        try {
            Map<String, Object> resp = aiService.analyzeImageUrl(Map.of("image_url", imageUrl));
            return resp != null ? (String) resp.get("category") : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ============================================================
    // DTO 변환 (aiCategory 없음)
    // ============================================================
    private AlbumResponse toResponse(Album album) {
        AlbumResponse res = new AlbumResponse();
        res.setAlbumId(album.getId());
        res.setGroupId(album.getGroup().getId());
        res.setTitle(album.getTitle());
        res.setDescription(album.getDescription());
        res.setImageUrl(album.getImageUrl());
        res.setCreatedAt(album.getCreatedAt());
        return res;
    }
}