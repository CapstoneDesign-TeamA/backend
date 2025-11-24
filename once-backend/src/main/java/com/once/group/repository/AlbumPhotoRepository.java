package com.once.group.repository;

import com.once.group.domain.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, Long> {
}
