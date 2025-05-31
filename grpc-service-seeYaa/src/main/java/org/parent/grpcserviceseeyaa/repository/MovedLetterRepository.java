package org.parent.grpcserviceseeyaa.repository;

import org.parent.grpcserviceseeyaa.entity.Letter;
import org.parent.grpcserviceseeyaa.entity.MovedLetter;
import org.parent.grpcserviceseeyaa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovedLetterRepository extends JpaRepository<MovedLetter, String> {
    Optional<MovedLetter> findByLetterId(String id);

    Optional<MovedLetter> findByLetterAndMovedBy(Letter letter, Users movedBy);
}
