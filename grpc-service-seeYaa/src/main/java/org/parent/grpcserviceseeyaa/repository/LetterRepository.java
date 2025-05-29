package org.parent.grpcserviceseeyaa.repository;

import org.parent.grpcserviceseeyaa.entity.Letter;
import org.parent.grpcserviceseeyaa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, String> {
    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);
    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);

    @Modifying
    @Transactional
    @Query("update Letter l set l.activeLetter = :active where l.id = :id")
    void updateActiveLetterById(@Param("id") String id, @Param("active") boolean active);
}
