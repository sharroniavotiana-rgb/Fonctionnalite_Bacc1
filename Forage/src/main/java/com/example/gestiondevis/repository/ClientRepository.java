package com.example.gestiondevis.repository;

import com.example.gestiondevis.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByNomContainingIgnoreCase(String nom);
    
    List<Client> findByContactContainingIgnoreCase(String contact);
    
    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.demandes WHERE c.id = :id")
    Client findByIdWithDemandes(@Param("id") Long id);
}