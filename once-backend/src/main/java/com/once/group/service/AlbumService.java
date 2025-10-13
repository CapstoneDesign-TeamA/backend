package com.once.group.service;

import com.once.group.domain.Album;
import com.once.group.domain.Group;
import com.once.group.dto.AlbumCreateRequest;
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

    public AlbumResponse createAlbum(Long groupId, AlbumCreateRequest request) {
        Group group = groupRepository.findById(groupId).orElse(null);

        Album album = new Album();
        album.setGroup(group);

        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setImageUrl(request.getImageUrl());
        album.setCreatedAt(LocalDateTime.now());

        Album saved = albumRepository.save(album);

        // DTO 변환
        AlbumResponse response = new AlbumResponse();
        response.setAlbumId(saved.getId());
        response.setGroupId(groupId);
        response.setTitle(saved.getTitle());
        response.setDescription(saved.getDescription());
        response.setImageUrl(saved.getImageUrl());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    public List<AlbumResponse> getAlbumsByGroup(Long groupId) {
        List<Album> albums = albumRepository.findByGroupId(groupId);

        return albums.stream().map(album -> {
            AlbumResponse res = new AlbumResponse();
            res.setAlbumId(album.getId());
            res.setGroupId(groupId);
            res.setTitle(album.getTitle());
            res.setDescription(album.getDescription());
            res.setImageUrl(album.getImageUrl());
            res.setCreatedAt(album.getCreatedAt());
            return res;
        }).collect(Collectors.toList());
    }

    public AlbumResponse updateAlbum(Long groupId, Long albumId, AlbumCreateRequest request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        // 그룹 검증 (선택적)
        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        // 수정 가능한 항목 업데이트
        if (request.getTitle() != null) album.setTitle(request.getTitle());
        if (request.getDescription() != null) album.setDescription(request.getDescription());
        if (request.getImageUrl() != null) album.setImageUrl(request.getImageUrl());

        Album updated = albumRepository.save(album);

        AlbumResponse res = new AlbumResponse();
        res.setAlbumId(updated.getId());
        res.setGroupId(groupId);
        res.setTitle(updated.getTitle());
        res.setDescription(updated.getDescription());
        res.setImageUrl(updated.getImageUrl());
        res.setCreatedAt(updated.getCreatedAt());

        return res;
    }

    public void deleteAlbum(Long groupId, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        // 그룹 검증 (해당 그룹에 속하지 않은 앨범 삭제 방지)
        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        albumRepository.delete(album);
    }
}
