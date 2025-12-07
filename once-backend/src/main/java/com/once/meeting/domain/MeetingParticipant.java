/**
 * File: MeetingParticipant.java
 * Description:
 *  - 모임 참여자 엔티티 정의
 *  - 특정 모임(meetingId)에 대한 사용자(userId)의 참여 상태 저장
 *  - 참여/불참 여부는 ParticipationStatus enum으로 관리
 */

package com.once.meeting.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meeting_participants")
public class MeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 참여 기록 ID

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId; // 모임 ID

    @Column(nullable = false)
    private Long userId; // 사용자 ID

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status; // 참여 상태 (ACCEPTED / DECLINED / NONE 등)
}