package com.example.gestionnotes.controller;

import com.example.gestionnotes.entity.*;
import com.example.gestionnotes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
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
    private CalculService calculService;
    
    // ============= CRUD Note =============
    
    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteService.findAll();
    }
    
    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(noteService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) {
        return noteService.createNote(
            note.getCandidat().getId(),
            note.getMatiere().getId(),
            note.getCorrecteur().getId(),
            note.getValeur()
        );
    }
    
    @PutMapping("/notes/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        try {
            // Vérifier que la note existe
            noteService.findById(id);
            
            // Mettre à jour
            note.setId(id);
            return ResponseEntity.ok(noteService.save(note));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ============= CRUD Parametre =============
    
    @GetMapping("/parametres")
    public List<Parametre> getAllParametres() {
        return parametreService.findAll();
    }
    
    @GetMapping("/parametres/{id}")
    public ResponseEntity<Parametre> getParametreById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(parametreService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/parametres")
    @ResponseStatus(HttpStatus.CREATED)
    public Parametre createParametre(@RequestBody Parametre parametre) {
        return parametreService.save(parametre);
    }
    
    @PutMapping("/parametres/{id}")
    public ResponseEntity<Parametre> updateParametre(@PathVariable Long id, @RequestBody Parametre parametre) {
        try {
            parametreService.findById(id); // Vérifie l'existence
            parametre.setId(id);
            return ResponseEntity.ok(parametreService.save(parametre));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/parametres/{id}")
    public ResponseEntity<Void> deleteParametre(@PathVariable Long id) {
        try {
            parametreService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ============= Endpoint pour le calcul de note finale =============
    
    @PostMapping("/calculer-note-finale")
    public ResponseEntity<Map<String, Object>> calculerNoteFinale(
            @RequestBody Map<String, Object> payload) {
        
        try {
            Long idCandidat = Long.parseLong(payload.get("idCandidat").toString());
            Long idMatiere = Long.parseLong(payload.get("idMatiere").toString());
            String saisieNotes = payload.get("notes").toString();
            
            List<Long> idsCorrecteurs;
            
            if (payload.containsKey("idsCorrecteurs") && payload.get("idsCorrecteurs") != null) {
                // Récupérer les IDs des correcteurs fournis
                List<?> idsList = (List<?>) payload.get("idsCorrecteurs");
                idsCorrecteurs = idsList.stream()
                        .map(id -> Long.parseLong(id.toString()))
                        .toList();
            } else {
                // Utiliser des correcteurs par défaut
                List<Double> notes = calculService.parserNotes(saisieNotes);
                correcteurService.validateEnoughCorrecteurs(notes.size());
                idsCorrecteurs = correcteurService.getFirstCorrecteurs(notes.size())
                        .stream()
                        .map(Correcteur::getId)
                        .toList();
            }
            
            Double noteFinale = calculService.traiterSaisieNotes(
                    saisieNotes, idCandidat, idMatiere, idsCorrecteurs);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("noteFinale", noteFinale);
            response.put("message", "Calcul effectué avec succès");
            response.put("candidatId", idCandidat);
            response.put("matiereId", idMatiere);
            response.put("notesSaisies", saisieNotes);
            response.put("nbCorrecteurs", idsCorrecteurs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Format de nombre invalide: " + e.getMessage()));
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Format de données invalide pour les correcteurs"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Erreur inattendue: " + e.getMessage()));
        }
    }
    
    // ============= Endpoints pour les données de référence =============
    
    @GetMapping("/candidats")
    public List<Candidat> getAllCandidats() {
        return candidatService.findAll();
    }
    
    @GetMapping("/matieres")
    public List<Matiere> getAllMatieres() {
        return matiereService.findAll();
    }
    
    @GetMapping("/correcteurs")
    public List<Correcteur> getAllCorrecteurs() {
        return correcteurService.findAll();
    }
    
    @GetMapping("/operateurs")
    public List<Operateur> getAllOperateurs() {
        return parametreService.findAllOperateurs();
    }
    
    @GetMapping("/resolutions")
    public List<Resolution> getAllResolutions() {
        return parametreService.findAllResolutions();
    }
}