package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.DoctorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorUserRepository extends JpaRepository<DoctorUser, String> {

    Optional<DoctorUser> findByUsername(String username);

    List<DoctorUser> findByIsActiveTrue();

    boolean existsByUsername(String username);
}
