package com.once.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vote_option_table")
@Getter
@Setter
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(name = "option_date")
    private LocalDate optionDate; // 투표할 날짜

    @Column(name = "vote_count")
    private int voteCount = 0; // 투표 결과 누적 기능
}