package org.parent.grpcserviceseeyaa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "letter")
@Entity
public class Letter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "toUser_id", referencedColumnName = "id")
    private Users userTo;

    @ManyToOne
    @JoinColumn(name = "byUser_id", referencedColumnName = "id")
    private Users userBy;

    @Column(nullable = false, length = 62)
    private String topic;

    @Column(nullable = false, length = 5000)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Boolean activeLetter = Boolean.FALSE;

    private LocalDateTime deleteTime;

    private Boolean watched;

    @OneToMany(mappedBy = "currentLetter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Files> files;
}
