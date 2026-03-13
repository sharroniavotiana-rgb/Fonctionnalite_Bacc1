package com.example.gestionnotes.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationService {
    
    /**
     * Valide qu'un objet n'est pas null
     */
    public void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Valide qu'une chaîne n'est pas vide
     */
    public void validateNotBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Valide une liste de notes
     */
    public List<Double> validateAndParseNotes(String saisieNotes) {
        validateNotBlank(saisieNotes, "La saisie des notes est requise");
        
        String[] notesArray = saisieNotes.split(";");
        if (notesArray.length < 2) {
            throw new IllegalArgumentException("Au moins 2 notes sont requises pour le calcul");
        }
        
        try {
            return java.util.Arrays.stream(notesArray)
                    .map(String::trim)
                    .map(Double::parseDouble)
                    .toList();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de note invalide. Utilisez des nombres séparés par ;");
        }
    }
    
    /**
     * Valide la correspondance entre notes et correcteurs
     */
    public void validateNotesCorrecteursMatch(int nbNotes, int nbCorrecteurs) {
        if (nbNotes != nbCorrecteurs) {
            throw new IllegalArgumentException(
                "Le nombre de notes (" + nbNotes + 
                ") doit correspondre au nombre de correcteurs (" + nbCorrecteurs + ")"
            );
        }
    }
}