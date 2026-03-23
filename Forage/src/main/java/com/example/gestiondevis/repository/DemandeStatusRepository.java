// repository/DemandeStatusRepository.java
package com.example.gestiondevis.repository;

import com.example.gestiondevis.entity.DemandeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeStatusRepository extends JpaRepository<DemandeStatus, Long> {
    List<DemandeStatus> findByDemandeIdOrderByDateDesc(Long demandeId);
}