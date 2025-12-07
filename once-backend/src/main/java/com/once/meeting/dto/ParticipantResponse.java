/**
 * File: ParticipantResponse.java
 * Description:
 *  - 모임 참여자 정보를 응답 형태로 전달하는 DTO
 *  - userId와 참여 상태(ACCEPTED / DECLINED)를 포함
 */

package com.once.meeting.dto;

import com.once.meeting.domain.MeetingParticipant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantResponse {

    private Long userId;   // 사용자 ID
    private String status; // 참여 상태 (ACCEPTED / DECLINED)

    /**
     * MeetingParticipant 엔티티를 ParticipantResponse DTO로 변환
     */
    public static ParticipantResponse from(MeetingParticipant p) {
        ParticipantResponse res = new ParticipantResponse();
        res.setUserId(p.getUserId());
        res.setStatus(p.getStatus().name());
        return res;
    }
}