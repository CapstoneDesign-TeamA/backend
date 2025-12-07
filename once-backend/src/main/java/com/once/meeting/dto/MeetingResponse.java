/**
 * File: MeetingResponse.java
 * Description:
 *  - 모임 조회 응답 DTO
 *  - 모임 기본 정보 + 참여 현황(참여 수, 내 참여 상태, 참여자/불참자 리스트) 포함
 *  - Meeting 엔티티와 관련 참여 데이터를 조합하여 생성됨
 */

package com.once.meeting.dto;

import com.once.meeting.domain.Meeting;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeetingResponse {

    private Long id;            // 모임 ID
    private Long groupId;       // 그룹 ID
    private Long creatorId;     // 모임 생성자 ID
    private String title;       // 제목
    private String description; // 설명

    private String startDate;   // 시작 날짜 (yyyy-MM-dd)
    private String endDate;     // 종료 날짜 (yyyy-MM-dd)
    private String time;        // 시간 (문자열)
    private String location;    // 장소

    private int participantCount; // ACCEPTED 인원 수
    private String myStatus;      // 현재 로그인 유저의 참여 상태 (ACCEPTED / DECLINED / null)

    // 참여자 정보 (nickname 우선, 없으면 username)
    private List<String> participants;
    private List<String> declined;

    /**
     * MeetingResponse 생성 메서드
     *
     * @param meeting 원본 Meeting 엔티티
     * @param participantCount ACCEPTED 참여자 수
     * @param myStatus 로그인 유저 참여 상태
     * @param participants 참여자 이름 목록
     * @param declined 불참자 이름 목록
     */
    public static MeetingResponse from(
            Meeting meeting,
            int participantCount,
            String myStatus,
            List<String> participants,
            List<String> declined
    ) {
        MeetingResponse res = new MeetingResponse();

        res.id = meeting.getId();
        res.groupId = meeting.getGroupId();
        res.creatorId = meeting.getCreatorId();
        res.title = meeting.getTitle();
        res.description = meeting.getDescription();

        res.startDate = meeting.getStartDate() != null
                ? meeting.getStartDate().toString()
                : null;

        res.endDate = meeting.getEndDate() != null
                ? meeting.getEndDate().toString()
                : null;

        res.time = meeting.getTime();
        res.location = meeting.getLocation();

        res.participantCount = participantCount;
        res.myStatus = myStatus;
        res.participants = participants;
        res.declined = declined;

        return res;
    }
}