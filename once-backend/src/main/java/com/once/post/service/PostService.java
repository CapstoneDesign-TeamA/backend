package com.once.post.service;

import com.once.ai.service.AiService;
import com.once.group.domain.Album;
import com.once.group.domain.Group;
import com.once.group.repository.AlbumRepository;
import com.once.group.repository.GroupRepository;
import com.once.group.service.GroupActivityCategoryService;
import com.once.meeting.domain.Meeting;
import com.once.meeting.repository.MeetingRepository;
import com.once.post.domain.Post;
import com.once.post.domain.PostImage;
import com.once.post.domain.PostLike;
import com.once.post.domain.PostType;
import com.once.group.service.ImageUploadService;
import com.once.post.dto.PostResponse;
import com.once.post.dto.PostUpdateRequest;

import com.once.post.repository.PostCommentRepository;
import com.once.post.repository.PostImageRepository;
import com.once.post.repository.PostLikeRepository;
import com.once.post.repository.PostRepository;

import com.once.user.domain.User;
import com.once.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostCommentRepository commentRepository;
    private final MeetingRepository meetingRepository;
    private final PostLikeRepository likeRepository;
    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;

    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository;

    private final AiService aiService;
    private final GroupActivityCategoryService groupActivityCategoryService;
    private final PostAiAnalysisService postAiAnalysisService;

    // ============================================================
    // 게시글 생성
    // ============================================================
    @Transactional
    public PostResponse createPost(
            Long groupId,
            Long userId,
            String content,
            String type,
            Long meetingId,
            List<MultipartFile> files
    ) throws IOException {

        if (type.equals("MEETING_REVIEW")) {

            if (meetingId == null) throw new RuntimeException("meetingId가 필요합니다.");

            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

            if (!meeting.getGroupId().equals(groupId))
                throw new RuntimeException("해당 그룹의 모임이 아닙니다.");

            if (!meeting.getEndDate().isBefore(LocalDate.now()))
                throw new RuntimeException("모임 종료 후에만 후기를 작성할 수 있습니다.");
        }

        Post post = Post.builder()
                .groupId(groupId)
                .userId(userId)
                .type(PostType.valueOf(type))
                .meetingId(meetingId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        int idx = 0;
        List<String> uploadedImageUrls = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {

                    String uploadedUrl = imageUploadService.uploadImage(file);
                    uploadedImageUrls.add(uploadedUrl);

                    String aiCategory = null;
                    try {
                        Map aiResponse = aiService.analyzeImageUrl(Map.of("image_url", uploadedUrl));
                        aiCategory = aiResponse != null ? (String) aiResponse.get("category") : null;

                        if (aiCategory != null) {
                            log.info("게시글 이미지 카테고리 저장: postId={}, userId={}, category={}", saved.getId(), userId, aiCategory);
                            groupActivityCategoryService.recordCategory(groupId, userId, aiCategory);
                        }

                    } catch (Exception ignored) {}

                    // ✅ 피드 이미지를 앨범에도 저장
                    try {
                        Group group = groupRepository.findById(groupId).orElse(null);
                        if (group != null) {
                            Album album = Album.builder()
                                    .group(group)
                                    .title("피드에서 업로드")
                                    .description(content != null && content.length() > 50
                                            ? content.substring(0, 50) + "..."
                                            : content)
                                    .imageUrl(uploadedUrl)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            albumRepository.save(album);
                            log.info("피드 이미지를 앨범에 저장: groupId={}, imageUrl={}", groupId, uploadedUrl);
                        }
                    } catch (Exception e) {
                        log.error("앨범 저장 실패: {}", e.getMessage());
                        // 앨범 저장 실패해도 피드는 계속 생성
                    }

                    PostImage img = PostImage.builder()
                            .postId(saved.getId())
                            .imageUrl(uploadedUrl)
                            .orderIndex(idx++)
                            .aiCategory(aiCategory)
                            .build();

                    postImageRepository.save(img);
                }
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<PostImage> imgs = postImageRepository.findByPostIdOrderByOrderIndexAsc(saved.getId());
        int likeCount = likeRepository.countByPostId(saved.getId());
        boolean myLiked = likeRepository.existsByPostIdAndUserId(saved.getId(), userId);
        int commentCount = commentRepository.countByPostId(saved.getId());

        return PostResponse.from(saved, user, imgs, likeCount, myLiked, commentCount);
    }


    // ============================================================
    // 피드 조회
    // ============================================================
    @Transactional(readOnly = true)
    public List<PostResponse> getFeed(Long groupId, Long userId) {

        List<Post> posts = postRepository.findByGroupIdOrderByCreatedAtDesc(groupId);

        return posts.stream().map(post -> {

            User postUser = userRepository.findById(post.getUserId())
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            List<PostImage> imgs =
                    postImageRepository.findByPostIdOrderByOrderIndexAsc(post.getId());

            int likeCount = likeRepository.countByPostId(post.getId());
            boolean myLiked = likeRepository.existsByPostIdAndUserId(post.getId(), userId);
            int commentCount = commentRepository.countByPostId(post.getId());

            return PostResponse.from(post, postUser, imgs, likeCount, myLiked, commentCount);

        }).toList();
    }


    // ============================================================
    // 게시글 삭제
    // ============================================================
    @Transactional
    public void deletePost(Long groupId, Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getGroupId().equals(groupId))
            throw new RuntimeException("해당 그룹의 게시글이 아닙니다.");

        if (!post.getUserId().equals(userId))
            throw new RuntimeException("삭제 권한이 없습니다.");

        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        postImageRepository.deleteByPostId(postId);

        postRepository.delete(post);
    }


    // ============================================================
    // 게시글 수정 (★ newImages → AI 분석 + group_activity_category 저장 추가)
    // ============================================================
    @Transactional
    public PostResponse updatePost(
            Long groupId,
            Long postId,
            Long userId,
            PostUpdateRequest req
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getGroupId().equals(groupId))
            throw new RuntimeException("해당 그룹의 게시글이 아닙니다.");

        if (!post.getUserId().equals(userId))
            throw new RuntimeException("수정 권한이 없습니다.");

        post.setContent(req.getContent());

        List<PostImage> existing = postImageRepository.findByPostIdOrderByOrderIndexAsc(postId);

        if (req.getKeepImageIds() == null || req.getKeepImageIds().isEmpty()) {
            postImageRepository.deleteByPostId(postId);
        } else {
            for (PostImage img : existing) {
                if (!req.getKeepImageIds().contains(img.getId())) {
                    postImageRepository.delete(img);
                }
            }
        }

        List<PostImage> remain = postImageRepository.findByPostIdOrderByOrderIndexAsc(postId);

        int idx = 0;
        for (PostImage img : remain) {
            img.setOrderIndex(idx++);
        }

        // ============================================================
        // ★ newImages 추가 시 AI 분석 로직 추가
        // ============================================================
        if (req.getNewImages() != null) {
            for (String url : req.getNewImages()) {

                // AI 카테고리 분석
                try {
                    Map aiResponse = aiService.analyzeImageUrl(Map.of("image_url", url));
                    String aiCategory = aiResponse != null ? (String) aiResponse.get("category") : null;

                    if (aiCategory != null) {
                        groupActivityCategoryService.recordCategory(groupId, userId, aiCategory);
                    }
                } catch (Exception ignored) {}

                // 이미지 저장
                PostImage img = PostImage.builder()
                        .postId(postId)
                        .imageUrl(url)
                        .orderIndex(idx++)
                        .build();

                postImageRepository.save(img);
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<PostImage> finalImgs =
                postImageRepository.findByPostIdOrderByOrderIndexAsc(postId);

        int likeCount = likeRepository.countByPostId(postId);
        boolean myLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        int commentCount = commentRepository.countByPostId(post.getId());

        return PostResponse.from(post, user, finalImgs, likeCount, myLiked, commentCount);
    }


    // ============================================================
    // 좋아요 토글 (★ user 객체를 요청자 userId 기준으로 변경)
    // ============================================================
    @Transactional
    public PostResponse toggleLike(Long groupId, Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getGroupId().equals(groupId))
            throw new RuntimeException("해당 그룹의 게시글이 아닙니다.");

        Optional<PostLike> existing = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            likeRepository.save(
                    PostLike.builder()
                            .postId(postId)
                            .userId(userId)
                            .build()
            );
        }

        // ================================
        // ★ user = 요청자 userId 로 변경됨
        // ================================
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<PostImage> imgs =
                postImageRepository.findByPostIdOrderByOrderIndexAsc(postId);

        int likeCount = likeRepository.countByPostId(postId);
        boolean myLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        int commentCount = commentRepository.countByPostId(post.getId());

        return PostResponse.from(post, user, imgs, likeCount, myLiked, commentCount);
    }
}