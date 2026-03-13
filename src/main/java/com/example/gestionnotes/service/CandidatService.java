package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.Candidat;
import com.example.gestionnotes.repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CandidatService {
    
    @Autowired
    private CandidatRepository candidatRepository;
    
    // ============= Méthodes CRUD =============
    
    public List<Candidat> findAll() {
        return candidatRepository.findAll();
    }
    
    public Optional<Candidat> findById(Long id) {
        return candidatRepository.findById(id);
    }
    
    public Optional<Candidat> findByPrenom(String prenom) {
        return candidatRepository.findByPrenom(prenom);
    }
    
    public Candidat save(Candidat candidat) {
        // Tu pourras ajouter de la validation ici plus tard
        return candidatRepository.save(candidat);
    }
    
    public void deleteById(Long id) {
        candidatRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return candidatRepository.existsById(id);
    }
    
    // ============= Méthodes métier spécifiques =============
    
    /**
     * Récupère un candidat ou lance une exception s'il n'existe pas
     */
    public Candidat getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'id: " + id));
    }
    
    /**
     * Vérifie si un candidat existe
     */
    public void validateExists(Long id) {
        if (!existsById(id)) {
            throw new RuntimeException("Candidat non trouvé avec l'id: " + id);
        }
    }
}