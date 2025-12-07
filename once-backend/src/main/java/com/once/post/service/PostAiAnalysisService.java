package com.once.post.service;

import com.once.ai.service.AiService;
import com.once.post.domain.Post;
import com.once.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 게시글 AI 분석 전용 서비스
 * @Async가 제대로 동작하려면 별도 클래스로 분리해야 함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostAiAnalysisService {

    private final AiService aiService;
    private final PostRepository postRepository;

    /**
     * 비동기 AI 게시글 분석
     * - 게시글 생성 후 백그라운드에서 실행
     * - 실패해도 게시글 자체는 유지
     */
    @Async
    @Transactional
    public void analyzePostAsync(Long postId, List<String> imageUrls, String content) {
        try {
            log.info("AI 게시글 분석 시작: postId={}, imageCount={}", postId, imageUrls.size());

            Map<String, Object> aiResult = aiService.analyzePost(imageUrls, content);

            if (aiResult != null) {
                String keywords = (String) aiResult.get("keywords");
                String sentiment = (String) aiResult.get("sentiment");
                String summary = (String) aiResult.get("summary");

                Post post = postRepository.findById(postId).orElse(null);
                if (post != null) {
                    post.setAiKeywords(keywords);
                    post.setAiSentiment(sentiment);
                    post.setAiSummary(summary);
                    postRepository.save(post);

                    log.info("AI 게시글 분석 완료: postId={}, keywords={}, sentiment={}",
                            postId, keywords, sentiment);
                }
            }

        } catch (Exception e) {
            log.error("AI 게시글 분석 실패: postId={}, error={}", postId, e.getMessage());
            // 분석 실패해도 게시글 자체는 유지
        }
    }
}

