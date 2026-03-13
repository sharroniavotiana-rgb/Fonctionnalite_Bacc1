package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.Correcteur;
import com.example.gestionnotes.repository.CorrecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CorrecteurService {
    
    @Autowired
    private CorrecteurRepository correcteurRepository;
    
    // ============= Méthodes CRUD =============
    
    public List<Correcteur> findAll() {
        return correcteurRepository.findAll();
    }
    
    public Optional<Correcteur> findById(Long id) {
        return correcteurRepository.findById(id);
    }
    
    public Optional<Correcteur> findByPrenom(String prenom) {
        return correcteurRepository.findByPrenom(prenom);
    }
    
    public Correcteur save(Correcteur correcteur) {
        return correcteurRepository.save(correcteur);
    }
    
    public void deleteById(Long id) {
        correcteurRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return correcteurRepository.existsById(id);
    }
    
    // ============= Méthodes métier =============
    
    public Correcteur getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Correcteur non trouvé avec l'id: " + id));
    }
    
    public void validateExists(Long id) {
        if (!existsById(id)) {
            throw new RuntimeException("Correcteur non trouvé avec l'id: " + id);
        }
    }
    
    /**
     * Vérifie si assez de correcteurs sont disponibles
     */
    public void validateEnoughCorrecteurs(int nombreRequis) {
        long nombreDisponible = correcteurRepository.count();
        if (nombreDisponible < nombreRequis) {
            throw new IllegalArgumentException(
                "Pas assez de correcteurs disponibles. Besoin: " + nombreRequis + 
                ", Disponibles: " + nombreDisponible
            );
        }
    }
    
    /**
     * Récupère les premiers correcteurs (pour l'assignation par défaut)
     */
    public List<Correcteur> getFirstCorrecteurs(int nombre) {
        validateEnoughCorrecteurs(nombre);
        return correcteurRepository.findAll().subList(0, nombre);
    }
    
    /**
     * Récupère une liste de correcteurs par leurs IDs
     */
    public List<Correcteur> findAllById(List<Long> ids) {
        return correcteurRepository.findAllById(ids);
    }
}