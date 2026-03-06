package com.iliasDev.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sessions")
public class SessionEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public SessionEntity(Long userId, LocalDateTime localDateTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.expiresAt = localDateTime;
    }
}
