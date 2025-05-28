package org.parent.grpcserviceseeyaa.entity;

import com.google.protobuf.Timestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "answers")
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String answerText;

    @ManyToOne
    @JoinColumn(name = "userBy_id", referencedColumnName = "id")
    private Users userByAnswered;

    @ManyToOne
    @JoinColumn(name = "current_letter_id", referencedColumnName = "id")
    private Letter currentLetter;

    @Column(nullable = false)
    private Timestamp createdAt;
}
