package com.once.meeting.dto;

import com.once.meeting.domain.MeetingParticipant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantResponse {

    private Long userId;
    private String status;

    public static ParticipantResponse from(MeetingParticipant p) {
        ParticipantResponse res = new ParticipantResponse();
        res.setUserId(p.getUserId());
        res.setStatus(p.getStatus().name());
        return res;
    }
}