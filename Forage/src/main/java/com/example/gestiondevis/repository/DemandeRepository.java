package com.example.gestiondevis.repository;

import com.example.gestiondevis.entity.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    
    List<Demande> findByClientId(Long clientId);
    
    List<Demande> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Demande> findByLieuContainingIgnoreCase(String lieu);
    
    List<Demande> findByDistrictContainingIgnoreCase(String district);
    
    @Query("SELECT d FROM Demande d LEFT JOIN FETCH d.client WHERE d.id = :id")
    Demande findByIdWithClient(@Param("id") Long id);
    
    @Query("SELECT d FROM Demande d LEFT JOIN FETCH d.client LEFT JOIN FETCH d.devis WHERE d.id = :id")
    Demande findByIdWithDetails(@Param("id") Long id);
}