package com.once.group.service;

import com.once.group.domain.Album;
import com.once.group.domain.AlbumPhoto;
import com.once.group.domain.Group;
import com.once.group.dto.AlbumResponse;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;

    /**
     * 앨범 생성
     * - 컨트롤러에서 이미지를 업로드하고, 여기서는 URL 리스트만 받아서 저장
     */
    public AlbumResponse createAlbum(Long groupId, String title, String description, List<String> imageUrls) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 그룹입니다."));

        Album album = new Album();
        album.setGroup(group);
        album.setTitle(title);
        album.setDescription(description);
        album.setCreatedAt(LocalDateTime.now());

        // 대표 이미지(썸네일) 설정: 첫 번째 사진
        if (imageUrls != null && !imageUrls.isEmpty()) {
            album.setImageUrl(imageUrls.get(0));
        }

        // 사진첩(여러 장) 저장
        if (imageUrls != null) {
            int order = 0;
            List<AlbumPhoto> photos = new ArrayList<>();
            for (String url : imageUrls) {
                AlbumPhoto photo = new AlbumPhoto();
                photo.setAlbum(album);
                photo.setImageUrl(url);
                photo.setSortOrder(order++);
                photos.add(photo);
            }
            album.setPhotos(photos);
        }

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
     * - title / description 은 항상 반영
     * - imageUrls가 null이면 기존 사진 유지
     * - imageUrls가 null이 아니면 기존 사진 전체 삭제 후 새 사진으로 교체
     */
    public AlbumResponse updateAlbum(Long groupId, Long albumId,
                                     String title, String description, List<String> imageUrls) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 앨범입니다."));

        if (album.getGroup() == null || !album.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("이 앨범은 해당 그룹에 속하지 않습니다.");
        }

        // 텍스트 정보 수정
        if (title != null) {
            album.setTitle(title);
        }
        if (description != null) {
            album.setDescription(description);
        }

        // 이미지가 넘어온 경우에만 사진 교체
        if (imageUrls != null) {
            // 대표 이미지 다시 설정
            if (!imageUrls.isEmpty()) {
                album.setImageUrl(imageUrls.get(0));
            } else {
                album.setImageUrl(null);
            }

            // 기존 사진들 제거
            album.getPhotos().clear();

            // 새 사진들 추가
            int order = 0;
            for (String url : imageUrls) {
                AlbumPhoto photo = new AlbumPhoto();
                photo.setAlbum(album);
                photo.setImageUrl(url);
                photo.setSortOrder(order++);
                album.getPhotos().add(photo);
            }
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
        res.setImageUrl(album.getImageUrl()); // 대표 이미지

        // 사진첩 전체 URL 리스트
        List<String> photoUrls = album.getPhotos().stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(AlbumPhoto::getImageUrl)
                .collect(Collectors.toList());
        res.setPhotos(photoUrls);

        res.setCreatedAt(album.getCreatedAt());
        return res;
    }
}
