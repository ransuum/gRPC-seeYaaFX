package org.parent.grpcserviceseeyaa.repository;

import org.parent.grpcserviceseeyaa.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {
}
