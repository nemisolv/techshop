package net.nemisolv.techshop.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.nemisolv.techshop.core._enum.MailType;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_emails")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ConfirmationEmail extends IdBaseEntity {

    // dont need to reference user object here ->
   private Long userId;

    private String token;

    @Enumerated(EnumType.STRING)
    private MailType type;

    private boolean revoked;



    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    @Column(name = "expired_at")

    private LocalDateTime expiredAt;



}
