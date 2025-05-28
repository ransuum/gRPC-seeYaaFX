package org.parent.grpcserviceseeyaa.repository;

import org.parent.grpcserviceseeyaa.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    Optional<Files> findByName(String fileName);
    List<Files> findAllByLetterId(String id);
}
