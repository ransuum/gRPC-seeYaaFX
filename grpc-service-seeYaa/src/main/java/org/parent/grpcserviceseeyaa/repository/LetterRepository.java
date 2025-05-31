package org.parent.grpcserviceseeyaa.repository;

import com.seeYaa.proto.email.TypeOfLetter;
import org.parent.grpcserviceseeyaa.entity.Letter;
import org.parent.grpcserviceseeyaa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, String> {
    List<Letter> findAllByTopicContainingAndUserBy(String topic, Users userBy);
    List<Letter> findAllByTopicContainingAndUserTo(String topic, Users userTo);

    @Query("select l from Letter l where l.userBy.email = :email and l.activeLetter = true order by l.createdAt desc")
    List<Letter> findSentActiveByUser(@Param("email") String email);

    @Query("select l from Letter l where l.userTo.email = :email and l.activeLetter = true order by l.createdAt desc")
    List<Letter> findInboxActiveByUser(@Param("email") String email);

    @Query("""
                select m.letter
                from MovedLetter m
                where m.movedBy.email = :email and m.typeOfLetter = :type
                order by m.letter.createdAt desc
            """)
    List<Letter> findAllLettersMovedByUser(@Param("email") String email, @Param("type") TypeOfLetter typeOfLetter);
}
