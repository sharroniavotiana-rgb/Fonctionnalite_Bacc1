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
        return candidatRepository.save(candidat);
    }
    
    public void deleteById(Long id) {
        candidatRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return candidatRepository.existsById(id);
    }
    
    
    public Candidat getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'id: " + id));
    }
    
    
    public void validateExists(Long id) {
        if (!existsById(id)) {
            throw new RuntimeException("Candidat non trouvé avec l'id: " + id);
        }
    }
}