package org.parent.grpcserviceseeyaa.entity;

import com.google.protobuf.Timestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "letter")
@Entity
public class Letter implements Serializable {
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
    private Timestamp createdAt;

    private Boolean activeLetter;

    private Timestamp deleteTime;

    @OneToMany(mappedBy = "currentLetter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @OneToMany(mappedBy = "letter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Files> files;
}
