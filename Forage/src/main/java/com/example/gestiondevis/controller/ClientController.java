package com.example.gestiondevis.controller;

import com.example.gestiondevis.dto.ClientDTO;
import com.example.gestiondevis.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    
    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.findAllClients());
        return "client/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("client", new ClientDTO());
        return "client/form";
    }
    
    @PostMapping("/save")
    public String saveClient(@Valid @ModelAttribute("client") ClientDTO clientDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "client/form";
        }
        
        try {
            clientService.saveClient(clientDTO);
            redirectAttributes.addFlashAttribute("success", "Client créé avec succès");
            return "redirect:/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du client: " + e.getMessage());
            return "redirect:/clients/new";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ClientDTO client = clientService.findClientById(id);
            model.addAttribute("client", client);
            return "client/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Client non trouvé");
            return "redirect:/clients";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @Valid @ModelAttribute("client") ClientDTO clientDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "client/form";
        }
        
        try {
            clientService.updateClient(id, clientDTO);
            redirectAttributes.addFlashAttribute("success", "Client mis à jour avec succès");
            return "redirect:/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/clients/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.deleteClient(id);
            redirectAttributes.addFlashAttribute("success", "Client supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/clients";
    }
    
    @GetMapping("/view/{id}")
    public String viewClient(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ClientDTO client = clientService.findClientById(id);
            model.addAttribute("client", client);
            return "client/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Client non trouvé");
            return "redirect:/clients";
        }
    }
    
    @GetMapping("/search")
    public String searchClients(@RequestParam(required = false) String nom, Model model) {
        if (nom != null && !nom.isEmpty()) {
            model.addAttribute("clients", clientService.searchClientsByNom(nom));
            model.addAttribute("searchNom", nom);
        } else {
            model.addAttribute("clients", clientService.findAllClients());
        }
        return "client/list";
    }
}