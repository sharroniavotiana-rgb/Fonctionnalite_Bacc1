package com.example.gestionnotes.controller;

import com.example.gestionnotes.entity.*;
import com.example.gestionnotes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Controller
@RequestMapping("/")
public class WebController {
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private MatiereService matiereService;
    
    @Autowired
    private CorrecteurService correcteurService;
    
    @Autowired
    private NoteService noteService;
    
    @Autowired
    private ParametreService parametreService;
    
    @Autowired
    private CalculService calculService;
    
    @Autowired
    private ValidationService validationService;
    
    @GetMapping
    public String index(Model model) {
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("correcteurs", correcteurService.findAll());
        model.addAttribute("notes", noteService.findAll());
        model.addAttribute("parametres", parametreService.findAll());
        return "index";
    }
    
    // ============= CRUD Notes =============
    
    @GetMapping("/notes")
    public String listNotes(Model model) {
        model.addAttribute("notes", noteService.findAll());
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("correcteurs", correcteurService.findAll());
        model.addAttribute("note", new Note());
        return "notes";
    }
    
    @PostMapping("/notes/save")
    public String saveNote(@ModelAttribute Note note, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier que les IDs sont fournis
            if (note.getCandidat() == null || note.getCandidat().getId() == null ||
                note.getMatiere() == null || note.getMatiere().getId() == null ||
                note.getCorrecteur() == null || note.getCorrecteur().getId() == null) {
                throw new RuntimeException("Tous les champs doivent être remplis");
            }
            
            // Créer la note via le service
            noteService.createNote(
                note.getCandidat().getId(),
                note.getMatiere().getId(),
                note.getCorrecteur().getId(),
                note.getValeur()
            );
            
            redirectAttributes.addFlashAttribute("success", "Note enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/notes";
    }
    
    @GetMapping("/notes/delete/{id}")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/notes";
    }
    
    // ============= CRUD Paramètres =============
    
    @GetMapping("/parametres")
    public String listParametres(Model model) {
        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("operateurs", parametreService.findAllOperateurs());
        model.addAttribute("resolutions", parametreService.findAllResolutions());
        model.addAttribute("parametre", new Parametre());
        return "parametres";
    }
    
    @PostMapping("/parametres/save")
    public String saveParametre(@ModelAttribute Parametre parametre, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier que les IDs sont fournis
            if (parametre.getMatiere() == null || parametre.getMatiere().getId() == null ||
                parametre.getOperateur() == null || parametre.getOperateur().getId() == null ||
                parametre.getResolution() == null || parametre.getResolution().getId() == null) {
                throw new RuntimeException("Tous les champs doivent être remplis");
            }
            
            // Récupérer les entités complètes
            Matiere matiere = matiereService.getOrThrow(parametre.getMatiere().getId());
            Operateur operateur = parametreService.findOperateurById(parametre.getOperateur().getId());
            Resolution resolution = parametreService.findResolutionById(parametre.getResolution().getId());
            
            // Associer les entités complètes
            parametre.setMatiere(matiere);
            parametre.setOperateur(operateur);
            parametre.setResolution(resolution);
            
            parametreService.save(parametre);
            redirectAttributes.addFlashAttribute("success", "Paramètre enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/parametres";
    }
    
    @GetMapping("/parametres/delete/{id}")
    public String deleteParametre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            parametreService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Paramètre supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/parametres";
    }
    
    // ============= Page de calcul de note finale =============
    
    @GetMapping("/calcul")
    public String showCalculationPage(Model model) {
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("correcteurs", correcteurService.findAll());
        
        // Ajouter un attribut pour le résultat (initialement null)
        if (!model.containsAttribute("noteFinale")) {
            model.addAttribute("noteFinale", null);
        }
        
        return "calcul";
    }
    
    @PostMapping("/calcul/resultat")
    public String calculateFinalNote(
            @RequestParam Long candidatId,
            @RequestParam Long matiereId,
            @RequestParam String notesSaisies,
            @RequestParam(required = false) List<Long> correcteurIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Valider les entrées
            validationService.validateNotNull(candidatId, "L'ID du candidat est requis");
            validationService.validateNotNull(matiereId, "L'ID de la matière est requis");
            
            // Parser et valider les notes
            List<Double> notesList = validationService.validateAndParseNotes(notesSaisies);
            
            // Calculer la différence avec détails
            CalculService.ResultatDifference resultatDiff = 
                calculService.calculerDifferenceAvecDetails(notesList);
            
            // Construire le détail du calcul HTML
            StringBuilder detailsCalcul = new StringBuilder("<p>Paires de notes :</p><ul>");
            for (String detail : resultatDiff.getDetailsPaires()) {
                detailsCalcul.append("<li>").append(detail).append("</li>");
            }
            detailsCalcul.append("</ul><p class='fw-bold'>Somme totale = ").append(resultatDiff.getSomme()).append("</p>");
            
            // Gestion des correcteurs
            List<Long> idsCorrecteurs = new ArrayList<>();
            if (correcteurIds != null && !correcteurIds.isEmpty()) {
                // Vérifier que le nombre de correcteurs correspond au nombre de notes
                validationService.validateNotesCorrecteursMatch(notesList.size(), correcteurIds.size());
                idsCorrecteurs.addAll(correcteurIds);
            } else {
                // Utiliser les correcteurs par défaut
                correcteurService.validateEnoughCorrecteurs(notesList.size());
                idsCorrecteurs = correcteurService.getFirstCorrecteurs(notesList.size())
                        .stream()
                        .map(Correcteur::getId)
                        .toList();
            }
            
            // Sauvegarder les notes et calculer la note finale
            Double noteFinale = calculService.traiterSaisieNotes(notesSaisies, candidatId, matiereId, idsCorrecteurs);
            
            // Déterminer quelle résolution a été appliquée
            String resolutionAppliquee = determinerResolution(noteFinale, notesList);
            
            // Récupérer les entités pour l'affichage
            Candidat candidat = candidatService.getOrThrow(candidatId);
            Matiere matiere = matiereService.getOrThrow(matiereId);
            
            // Préparer les données pour la vue
            model.addAttribute("noteFinale", noteFinale);
            model.addAttribute("candidat", candidat);
            model.addAttribute("matiere", matiere);
            model.addAttribute("notesSaisies", notesSaisies);
            model.addAttribute("nbNotes", notesList.size());
            model.addAttribute("differenceCalculee", resultatDiff.getSomme());
            model.addAttribute("detailsPaires", resultatDiff.getDetailsPaires());
            model.addAttribute("detailsCalcul", detailsCalcul.toString());
            model.addAttribute("resolutionAppliquee", resolutionAppliquee);
            
            // Récupérer les notes existantes pour ce candidat et cette matière
            List<Note> notesExistantes = noteService.findByCandidatIdAndMatiereId(candidatId, matiereId);
            model.addAttribute("notesExistantes", notesExistantes);
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur inattendue: " + e.getMessage());
        }
        
        // Recharger les données pour le formulaire
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("correcteurs", correcteurService.findAll());
        
        return "calcul";
    }
    
    @PostMapping("/calcul/resultat-auto")
    public String calculateFinalNoteAuto(
            @RequestParam Long candidatId,
            @RequestParam Long matiereId,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Récupérer les notes existantes
            List<Note> notesExistantes = noteService.findByCandidatIdAndMatiereId(candidatId, matiereId);
            
            if (notesExistantes.isEmpty()) {
                throw new IllegalArgumentException("Aucune note existante pour ce candidat et cette matière");
            }
            
            // Construire la chaîne de notes séparées par ;
            StringBuilder notesSaisies = new StringBuilder();
            List<Double> notesList = new ArrayList<>();
            
            for (int i = 0; i < notesExistantes.size(); i++) {
                Double note = notesExistantes.get(i).getValeur();
                notesList.add(note);
                if (i > 0) notesSaisies.append(";");
                notesSaisies.append(note);
            }
            
            // Calculer la note finale
            Double noteFinale = calculService.determinerNoteFinale(candidatId, matiereId);
            
            // Calculer la différence avec détails
            CalculService.ResultatDifference resultatDiff = 
                calculService.calculerDifferenceAvecDetails(notesList);
            
            // Déterminer la résolution appliquée
            String resolutionAppliquee = determinerResolution(noteFinale, notesList);
            
            // Ajouter les attributs au modèle
            model.addAttribute("noteFinale", noteFinale);
            model.addAttribute("candidat", candidatService.getOrThrow(candidatId));
            model.addAttribute("matiere", matiereService.getOrThrow(matiereId));
            model.addAttribute("notesSaisies", notesSaisies.toString());
            model.addAttribute("nbNotes", notesList.size());
            model.addAttribute("differenceCalculee", resultatDiff.getSomme());
            model.addAttribute("detailsPaires", resultatDiff.getDetailsPaires());
            model.addAttribute("resolutionAppliquee", resolutionAppliquee);
            model.addAttribute("typeCalcul", "Automatique (notes existantes)");
            model.addAttribute("notesExistantes", notesExistantes);
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }
        
        // Recharger les données
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("matieres", matiereService.findAll());
        model.addAttribute("correcteurs", correcteurService.findAll());
        
        return "calcul";
    }
    
    /**
     * Détermine quelle résolution a été appliquée
     */
    private String determinerResolution(Double noteFinale, List<Double> notes) {
        if (notes == null || notes.isEmpty()) {
            return "inconnue";
        }
        
        double min = Collections.min(notes);
        double max = Collections.max(notes);
        double moyenne = notes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // Utiliser une petite marge d'erreur pour les comparaisons de doubles
        double epsilon = 0.001;
        
        if (Math.abs(noteFinale - min) < epsilon) {
            return "plus petit";
        } else if (Math.abs(noteFinale - max) < epsilon) {
            return "plus grand";
        } else if (Math.abs(noteFinale - moyenne) < epsilon) {
            return "moyenne";
        } else {
            return "moyenne"; // Par défaut
        }
    }
    
    // ============= Endpoints AJAX =============
    
    @GetMapping("/calcul/notes-candidat")
    @ResponseBody
    public List<Note> getNotesCandidat(@RequestParam Long candidatId, @RequestParam Long matiereId) {
        return noteService.findByCandidatIdAndMatiereId(candidatId, matiereId);
    }
    
    @GetMapping("/calcul/verifier-correcteurs")
    @ResponseBody
    public String verifierCorrecteurs(@RequestParam int nbNotes) {
        try {
            correcteurService.validateEnoughCorrecteurs(nbNotes);
            return "OK";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}