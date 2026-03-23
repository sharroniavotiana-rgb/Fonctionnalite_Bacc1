package com.example.gestiondevis.controller;

import com.example.gestiondevis.dto.DemandeDTO;
import com.example.gestiondevis.service.ClientService;
import com.example.gestiondevis.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/demandes")
@RequiredArgsConstructor
public class DemandeController {
    
    private final DemandeService demandeService;
    private final ClientService clientService;
    
    @GetMapping
    public String listDemandes(Model model) {
        model.addAttribute("demandes", demandeService.findAllDemandes());
        return "demande/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("demande", new DemandeDTO());
        model.addAttribute("clients", clientService.findAllClients());
        return "demande/form";
    }
    
    @PostMapping("/save")
    public String saveDemande(@Valid @ModelAttribute("demande") DemandeDTO demandeDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.findAllClients());
            return "demande/form";
        }
        
        try {
            demandeService.saveDemande(demandeDTO);
            redirectAttributes.addFlashAttribute("success", "Demande créée avec succès");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/demandes/new";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DemandeDTO demande = demandeService.findDemandeById(id);
            model.addAttribute("demande", demande);
            model.addAttribute("clients", clientService.findAllClients());
            return "demande/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateDemande(@PathVariable Long id,
                                @Valid @ModelAttribute("demande") DemandeDTO demandeDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.findAllClients());
            return "demande/form";
        }
        
        try {
            demandeService.updateDemande(id, demandeDTO);
            redirectAttributes.addFlashAttribute("success", "Demande mise à jour avec succès");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/demandes/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeService.deleteDemande(id);
            redirectAttributes.addFlashAttribute("success", "Demande supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/demandes";
    }
    
    @GetMapping("/view/{id}")
    public String viewDemande(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DemandeDTO demande = demandeService.findDemandeById(id);
            model.addAttribute("demande", demande);
            return "demande/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        }
    }
    
    @GetMapping("/client/{clientId}")
    public String listDemandesByClient(@PathVariable Long clientId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("demandes", demandeService.findDemandesByClientId(clientId));
            model.addAttribute("clientId", clientId);
            return "demande/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des demandes");
            return "redirect:/demandes";
        }
    }
    
    @GetMapping("/search")
    public String searchDemandes(@RequestParam(required = false) String lieu, Model model) {
        if (lieu != null && !lieu.isEmpty()) {
            model.addAttribute("demandes", demandeService.searchDemandesByLieu(lieu));
            model.addAttribute("searchLieu", lieu);
        } else {
            model.addAttribute("demandes", demandeService.findAllDemandes());
        }
        return "demande/list";
    }
}