package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.*;
import com.example.gestionnotes.repository.ParametreRepository;
import com.example.gestionnotes.repository.OperateurRepository;    
import com.example.gestionnotes.repository.ResolutionRepository;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParametreService {
    
    @Autowired
    private ParametreRepository parametreRepository;
    
    @Autowired
    private MatiereService matiereService;
    
    @Autowired
    private OperateurRepository operateurRepository;  
    
    @Autowired
    private ResolutionRepository resolutionRepository; 
    
    
    
    public List<Parametre> findAll() {
        return parametreRepository.findAll();
    }
    
    public Parametre findById(Long id) {
        return parametreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètre non trouvé avec l'id: " + id));
    }
    
    public Parametre save(Parametre parametre) {
        return parametreRepository.save(parametre);
    }
    
    public void deleteById(Long id) {
        parametreRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return parametreRepository.existsById(id);
    }
    
    
    
    public List<Parametre> findByMatiereId(Long matiereId) {
        return parametreRepository.findByMatiereIdOrderByDifference(matiereId);
    }
    
    
    
    public List<Operateur> findAllOperateurs() {
        return operateurRepository.findAll();
    }
    
    public List<Resolution> findAllResolutions() {
        return resolutionRepository.findAll();
    }
    
    public Operateur findOperateurById(Long id) {
        return operateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opérateur non trouvé avec l'id: " + id));
    }
    
    public Resolution findResolutionById(Long id) {
        return resolutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Résolution non trouvée avec l'id: " + id));
    }
    
    /**
     * Compare deux valeurs selon un opérateur
     */
    public boolean comparer(Double valeur1, Double valeur2, String operateur) {
        return switch (operateur) {
            case "<" -> valeur1 < valeur2;
            case ">" -> valeur1 > valeur2;
            case "<=" -> valeur1 <= valeur2;
            case ">=" -> valeur1 >= valeur2;
            default -> false;
        };
    }
    
    /**
     * Trouve le paramètre applicable pour une différence donnée
     */
    public Parametre trouverParametreApplicable(Long matiereId, Double difference) {
        List<Parametre> parametres = findByMatiereId(matiereId);
        
        for (Parametre p : parametres) {
            if (comparer(difference, p.getDifference(), p.getOperateur().getSymbole())) {
                return p;
            }
        }
        return null;
    }
}