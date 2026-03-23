package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.Parametre;
import com.example.gestionnotes.entity.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CalculService {
    
    @Autowired
    private NoteService noteService;
    
    @Autowired
    private ParametreService parametreService;
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private MatiereService matiereService;
    
    @Autowired
    private CorrecteurService correcteurService;
    
    @Autowired
    private ValidationService validationService;
    
    /*
     * Calcule la différence entre plusieurs notes
     * Raha note 2 de entre anizy 2 
     * Raha entre note + de 2 de ny somme anle difference anzareo
     */
    public Double calculerDifference(List<Double> notes) {
        if (notes == null || notes.size() < 2) {
            return 0.0;
        }
        
        Double sommeDifferences = 0.0;
        
        
        for (int i = 0; i < notes.size(); i++) {
            for (int j = i + 1; j < notes.size(); j++) {
                sommeDifferences += Math.abs(notes.get(i) - notes.get(j));
            }
        }
        
        return sommeDifferences;
    }
    
    /**
     * Calcule la différence et retourne aussi les détails des paires de eealefa any am classe interne ResultatDifference
     */
    public ResultatDifference calculerDifferenceAvecDetails(List<Double> notes) {
        if (notes == null || notes.size() < 2) {
            return new ResultatDifference(0.0, new ArrayList<>());
        }
        
        Double sommeDifferences = 0.0;
        List<String> detailsPaires = new ArrayList<>();
        
        for (int i = 0; i < notes.size(); i++) {
            for (int j = i + 1; j < notes.size(); j++) {
                double diff = Math.abs(notes.get(i) - notes.get(j));
                sommeDifferences += diff;
                detailsPaires.add(String.format("%.1f - %.1f = %.1f", 
                    notes.get(i), notes.get(j), diff));
            }
        }
        
        return new ResultatDifference(sommeDifferences, detailsPaires);
    }
    
    /**
     * Calcule anah moyenne amle note raha moyenne no reslution 
     */
    public Double calculerMoyenne(List<Double> notes) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }
        return notes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
    
    /**
     * mi appliquer ny resolution zay mifandraiky am parametre 
     */
    public Double appliquerResolution(List<Double> notes, String resolution) {
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }
        
        String resolutionLower = resolution.toLowerCase();
        
        if (resolutionLower.contains("plus petit") || resolutionLower.contains("min")) {
            return Collections.min(notes);
        } else if (resolutionLower.contains("plus grand") || resolutionLower.contains("max")) {
            return Collections.max(notes);
        } else { 
            return calculerMoyenne(notes);
        }
    }
    
    /**
     * Détermine la note finale selon les parametres 
     */
    public Double determinerNoteFinale(Long idCandidat, Long idMatiere) {
        
        candidatService.validateExists(idCandidat);
        matiereService.validateExists(idMatiere);
        
        
        List<Note> notes = noteService.findByCandidatIdAndMatiereId(idCandidat, idMatiere);
        
        if (notes.isEmpty()) {
            return null;
        }
        
        
        List<Double> valeursNotes = notes.stream()
                .map(Note::getValeur)
                .toList();
        
        
        Double difference = calculerDifference(valeursNotes);
        
        
        Parametre parametreApplicable = parametreService.trouverParametreApplicable(idMatiere, difference);
        
        if (parametreApplicable == null) {
            
            return calculerMoyenne(valeursNotes);
        }
        
        
        return appliquerResolution(valeursNotes, parametreApplicable.getResolution().getNom());
    }
    
    /**
     * Traite une saisie de notes et calcule la note finale
     */
    @Transactional
    public Double traiterSaisieNotes(String saisieNotes, Long idCandidat, Long idMatiere, List<Long> idsCorrecteurs) {
        
        validationService.validateNotNull(idCandidat, "L'ID du candidat est requis");
        validationService.validateNotNull(idMatiere, "L'ID de la matière est requis");
        
        
        List<Double> notes = validationService.validateAndParseNotes(saisieNotes);
        
        
        validationService.validateNotesCorrecteursMatch(notes.size(), idsCorrecteurs.size());
        
        
        noteService.createNotes(idCandidat, idMatiere, notes, idsCorrecteurs);
        
        
        return determinerNoteFinale(idCandidat, idMatiere);
    }
    

    @Transactional
    public Double traiterSaisieNotes(String saisieNotes, Long idCandidat, Long idMatiere) {
        
        validationService.validateNotNull(idCandidat, "L'ID du candidat est requis");
        validationService.validateNotNull(idMatiere, "L'ID de la matière est requis");
        
        
        List<Double> notes = validationService.validateAndParseNotes(saisieNotes);
        
        
        correcteurService.validateEnoughCorrecteurs(notes.size());
        
        
        List<Long> idsCorrecteurs = correcteurService.getFirstCorrecteurs(notes.size())
                .stream()
                .map(c -> c.getId())
                .toList();
        
        
        noteService.createNotes(idCandidat, idMatiere, notes, idsCorrecteurs);
        
        
        return determinerNoteFinale(idCandidat, idMatiere);
    }
    
    /**
     * Classe interne pour retourner le résultat du calcul avec détails
     */
    public static class ResultatDifference {
        private final Double somme;
        private final List<String> detailsPaires;
        
        public ResultatDifference(Double somme, List<String> detailsPaires) {
            this.somme = somme;
            this.detailsPaires = detailsPaires;
        }
        
        public Double getSomme() { return somme; }
        public List<String> getDetailsPaires() { return detailsPaires; }
    }

    /**
 * Parse une chaîne de notes séparées par des points-virgules
 * @param saisieNotes Chaîne formatée comme "12;15;14"
 * @return Liste de Double
 * @throws IllegalArgumentException si le format est invalide
 */
public List<Double> parserNotes(String saisieNotes) {
    if (saisieNotes == null || saisieNotes.trim().isEmpty()) {
        throw new IllegalArgumentException("La saisie des notes est requise");
    }
    
    String[] notesArray = saisieNotes.split(";");
    List<Double> notes = new ArrayList<>();
    
    for (String noteStr : notesArray) {
        try {
            notes.add(Double.parseDouble(noteStr.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de note invalide: " + noteStr);
        }
    }
    
    if (notes.size() < 2) {
        throw new IllegalArgumentException("Au moins 2 notes sont requises pour le calcul");
    }
    
    return notes;
}
}