package com.example.gestionnotes.service;

import com.example.gestionnotes.entity.*;
import com.example.gestionnotes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private MatiereService matiereService;
    
    @Autowired
    private CorrecteurService correcteurService;
    
    // ============= Méthodes CRUD =============
    
    public List<Note> findAll() {
        return noteRepository.findAll();
    }
    
    public Note findById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec l'id: " + id));
    }
    
    public Note save(Note note) {
        return noteRepository.save(note);
    }
    
    public void deleteById(Long id) {
        noteRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return noteRepository.existsById(id);
    }
    
    // ============= Méthodes de recherche =============
    
    public List<Note> findByCandidatAndMatiere(Candidat candidat, Matiere matiere) {
        return noteRepository.findByCandidatAndMatiere(candidat, matiere);
    }
    
    public List<Note> findByCandidatIdAndMatiereId(Long candidatId, Long matiereId) {
        return noteRepository.findByCandidatIdAndMatiereId(candidatId, matiereId);
    }
    
    // ============= Méthodes métier =============
    
    /**
     * Supprime toutes les notes d'un candidat pour une matière
     */
    public void deleteByCandidatAndMatiere(Long candidatId, Long matiereId) {
        List<Note> notes = findByCandidatIdAndMatiereId(candidatId, matiereId);
        if (!notes.isEmpty()) {
            noteRepository.deleteAll(notes);
        }
    }
    
    /**
     * Crée une nouvelle note avec validation des entités
     */
    public Note createNote(Long candidatId, Long matiereId, Long correcteurId, Double valeur) {
        Candidat candidat = candidatService.getOrThrow(candidatId);
        Matiere matiere = matiereService.getOrThrow(matiereId);
        Correcteur correcteur = correcteurService.getOrThrow(correcteurId);
        
        Note note = new Note();
        note.setCandidat(candidat);
        note.setMatiere(matiere);
        note.setCorrecteur(correcteur);
        note.setValeur(valeur);
        
        return save(note);
    }
    
    /**
     * Crée plusieurs notes pour un candidat et une matière
     */
    public List<Note> createNotes(Long candidatId, Long matiereId, 
                                   List<Double> valeurs, List<Long> correcteurIds) {
        if (valeurs.size() != correcteurIds.size()) {
            throw new IllegalArgumentException(
                "Le nombre de notes (" + valeurs.size() + 
                ") ne correspond pas au nombre de correcteurs (" + correcteurIds.size() + ")"
            );
        }
        
        // Supprimer les anciennes notes
        deleteByCandidatAndMatiere(candidatId, matiereId);
        
        // Créer les nouvelles notes
        for (int i = 0; i < valeurs.size(); i++) {
            createNote(candidatId, matiereId, correcteurIds.get(i), valeurs.get(i));
        }
        
        return findByCandidatIdAndMatiereId(candidatId, matiereId);
    }
}