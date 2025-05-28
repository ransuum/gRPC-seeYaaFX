package org.parent.grpcserviceseeyaa.repository;

import org.parent.grpcserviceseeyaa.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);
}
