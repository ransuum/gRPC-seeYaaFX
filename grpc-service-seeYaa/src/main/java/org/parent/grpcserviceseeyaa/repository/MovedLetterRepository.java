package org.parent.grpcserviceseeyaa.repository;

import com.google.protobuf.Timestamp;
import com.seeYaa.proto.email.TypeOfLetter;
import org.parent.grpcserviceseeyaa.entity.MovedLetter;
import org.parent.grpcserviceseeyaa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovedLetterRepository extends JpaRepository<MovedLetter, String> {
    List<MovedLetter> findByWillDeleteAtBeforeAndTypeOfLetter(Timestamp now, TypeOfLetter typeOfLetter);
    List<MovedLetter> findAllByMovedByAndTypeOfLetter(Users users, TypeOfLetter typeOfLetter);
    Optional<MovedLetter> findByLetterId(String id);
}
