package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.*;
import com.example.gestionnotes.repository.ParametreRepository;
import com.example.gestionnotes.repository.OperateurRepository;    
import com.example.gestionnotes.repository.ResolutionRepository;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
    
 public Parametre trouverParametreApplicable(Long matiereId, Double difference) {
    List<Parametre> tousParametres = findByMatiereId(matiereId);
    
    if (tousParametres.isEmpty()) {
        return null;
    }
    
    
    List<Parametre> applicables = new ArrayList<>();
    for (Parametre p : tousParametres) {
        if (comparer(difference, p.getDifference(), p.getOperateur().getSymbole())) {
            applicables.add(p);
        }
    }
    
    
    if (applicables.isEmpty()) {
        return null;
    }
    
    
    if (applicables.size() == 1) {
        return applicables.get(0);
    }
    
    
    List<ParametreAvecEcart> avecEcarts = new ArrayList<>();
    for (Parametre p : applicables) {
        double ecart = Math.abs(difference - p.getDifference());
        avecEcarts.add(new ParametreAvecEcart(p, ecart));
    }
    
    
    double minEcart = avecEcarts.stream()
            .mapToDouble(pe -> pe.ecart)
            .min()
            .orElse(Double.MAX_VALUE);
    
    
    List<ParametreAvecEcart> meilleursEcarts = avecEcarts.stream()
            .filter(pe -> pe.ecart == minEcart)
            .toList();
    
    
    if (meilleursEcarts.size() == 1) {
        return meilleursEcarts.get(0).parametre;
    }
    
    
    return meilleursEcarts.stream()
            .map(pe -> pe.parametre)
            .min(Comparator.comparingDouble(Parametre::getDifference))
            .orElse(null);
}

private static class ParametreAvecEcart {
    Parametre parametre;
    double ecart;
    
    ParametreAvecEcart(Parametre parametre, double ecart) {
        this.parametre = parametre;
        this.ecart = ecart;
    }
}
}