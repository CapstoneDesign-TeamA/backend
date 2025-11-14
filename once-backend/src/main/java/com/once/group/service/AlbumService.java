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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;

    // 이미지업로드용 url
    private static final String PAR_BASE_URL = "https://objectstorage.ca-toronto-1.oraclecloud.com/p/w0f0KY74maKWN4fGv7beOkr_9RSBHwAcwJ52BfM37lR6d7H5w2S2edoAVrZWBCO9/n/yzhu49nqu7rk/b/once-bucket/o/";

    public AlbumResponse createAlbum
            (Long groupId, String title, String description, MultipartFile file) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            try {
                String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String uploadUrl = PAR_BASE_URL + uniqueFileName;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(file.getContentType()));

                HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<String> response = restTemplate.exchange(
                        uploadUrl, HttpMethod.PUT, requestEntity, String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    imageUrl = uploadUrl; // 업로드 성공하면 url을 db에 저장
                } else {
                    throw new RuntimeException("업로드 실패: " + response.getStatusCode());
                }

            } catch (IOException e) {
                throw new RuntimeException("파일 읽기 실패: " + e.getMessage());
            }
        }

        // 앨범 생성 및 DB 저장
        Album album = new Album();
        album.setGroup(group);
        album.setTitle(title);
        album.setDescription(description);
        album.setImageUrl(imageUrl);
        album.setCreatedAt(LocalDateTime.now());

        Album saved = albumRepository.save(album);

        // DTO
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

    public AlbumResponse updateAlbum(Long groupId, Long albumId, String title, String description, String imageUrl) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        // 그룹 검증 (선택적)
        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        // 수정용
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
