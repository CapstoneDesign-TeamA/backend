package com.once.user.dto.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class UserActivityLog {
    private Long id;
    @Getter
    @Setter
    private Long user_id;
    @Getter
    @Setter
    private String activity_type;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String ip_address;
    private String user_agent;
    @Setter
    @Getter
    private LocalDateTime created_at;


    public UserActivityLog() {}

    // 带参数的构造函数
    public UserActivityLog(Long user_id, String activity_type, String description) {
        this.user_id = user_id;
        this.activity_type = activity_type;
        this.description = description;
        this.created_at = LocalDateTime.now();
    }

//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return user_id; }
    public void setUserId(Long user_id) { this.user_id = user_id; }

    public String getActivityType() { return activity_type; }
    public void setActivityType(String activity_type) { this.activity_type = activity_type; }

    public String getUserAgent() { return user_agent; }
    public void setUserAgent(String user_agent) { this.user_agent = user_agent; }
    public String getIpAddress() { return ip_address; }
    public void setIpAddress(String ipAddress) { this.ip_address = ip_address; }

    public void setCreatedAt(LocalDateTime created_at) { this.created_at = created_at; }
    public LocalDateTime getcreatedAt(LocalDateTime created_at) { return created_at; }
}