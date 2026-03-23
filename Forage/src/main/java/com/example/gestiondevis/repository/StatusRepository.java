package com.example.gestiondevis.repository;

import com.example.gestiondevis.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    // Vous pouvez ajouter des méthodes de recherche si nécessaire
    Status findByLibelle(String libelle);
}