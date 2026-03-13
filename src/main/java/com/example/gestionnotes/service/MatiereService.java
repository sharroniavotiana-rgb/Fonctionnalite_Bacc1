package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.Matiere;
import com.example.gestionnotes.repository.MatiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatiereService {
    
    @Autowired
    private MatiereRepository matiereRepository;
    
    // ============= Méthodes CRUD =============
    
    public List<Matiere> findAll() {
        return matiereRepository.findAll();
    }
    
    public Optional<Matiere> findById(Long id) {
        return matiereRepository.findById(id);
    }
    
    public Optional<Matiere> findByNom(String nom) {
        return matiereRepository.findByNom(nom);
    }
    
    public Matiere save(Matiere matiere) {
        return matiereRepository.save(matiere);
    }
    
    public void deleteById(Long id) {
        matiereRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return matiereRepository.existsById(id);
    }
    
    // ============= Méthodes métier =============
    
    public Matiere getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id: " + id));
    }
    
    public void validateExists(Long id) {
        if (!existsById(id)) {
            throw new RuntimeException("Matière non trouvée avec l'id: " + id);
        }
    }
}