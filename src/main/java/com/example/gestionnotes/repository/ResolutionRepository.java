package com.example.gestionnotes.repository;  // Vérifie que le package est correct

import com.example.gestionnotes.entity.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    Optional<Resolution> findByNom(String nom);
}