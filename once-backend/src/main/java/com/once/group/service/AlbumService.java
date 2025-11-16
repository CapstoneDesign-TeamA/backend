package com.once.group.service;

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;

    /**
     * 앨범 생성
     * - 이미지는 컨트롤러에서 업로드하고, 여기서는 imageUrl 문자열만 받아서 저장
     */
    public AlbumResponse createAlbum(Long groupId, String title, String description, String imageUrl) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Album album = new Album();
        album.setGroup(group);
        album.setTitle(title);
        album.setDescription(description);
        album.setImageUrl(imageUrl);
        album.setCreatedAt(LocalDateTime.now()); // 엔티티에서 자동 세팅 안 하면 유지

        Album saved = albumRepository.save(album);
        return toResponse(saved);
    }

    /**
     * 그룹별 앨범 목록 조회
     */
    public List<AlbumResponse> getAlbumsByGroup(Long groupId) {
        List<Album> albums = albumRepository.findByGroupId(groupId);

        return albums.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 앨범 수정
     * - title / description / imageUrl 중 null이 아닌 값만 변경
     */
    public AlbumResponse updateAlbum(Long groupId, Long albumId,
                                     String title, String description, String imageUrl) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        if (title != null) {
            album.setTitle(title);
        }
        if (description != null) {
            album.setDescription(description);
        }
        if (imageUrl != null) {
            album.setImageUrl(imageUrl);
        }

        Album updated = albumRepository.save(album);
        return toResponse(updated);
    }

    /**
     * 앨범 삭제
     */
    public void deleteAlbum(Long groupId, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        albumRepository.delete(album);
    }

    /**
     * 공통 DTO 매핑
     */
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