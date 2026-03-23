// service/impl/DemandeServiceImpl.java
package com.example.gestiondevis.service.impl;

import com.example.gestiondevis.dto.DemandeDTO;
import com.example.gestiondevis.entity.Client;
import com.example.gestiondevis.entity.Demande;
import com.example.gestiondevis.entity.DemandeStatus;
import com.example.gestiondevis.entity.Status;
import com.example.gestiondevis.repository.ClientRepository;
import com.example.gestiondevis.repository.DemandeRepository;
import com.example.gestiondevis.repository.DemandeStatusRepository;
import com.example.gestiondevis.repository.StatusRepository;
import com.example.gestiondevis.service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeServiceImpl implements DemandeService {
    
    private final DemandeRepository demandeRepository;
    private final ClientRepository clientRepository;
    private final DemandeStatusRepository demandeStatusRepository;
    private final StatusRepository statusRepository;
    
    @Override
    public List<DemandeDTO> findAllDemandes() {
        return demandeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DemandeDTO findDemandeById(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'id: " + id));
        
        DemandeDTO dto = convertToDTO(demande);
        
        // Récupérer le dernier statut de la demande
        List<DemandeStatus> statusList = demandeStatusRepository.findByDemandeIdOrderByDateDesc(id);
        if (!statusList.isEmpty()) {
            dto.setStatusLibelle(statusList.get(0).getStatus().getLibelle());
        }
        
        return dto;
    }
    
    @Override
    @Transactional
    public DemandeDTO saveDemande(DemandeDTO demandeDTO) {
        // 1. Récupérer le client
        Client client = clientRepository.findById(demandeDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + demandeDTO.getClientId()));
        
        // 2. Créer la demande
        Demande demande = new Demande();
        demande.setDate(demandeDTO.getDate());
        demande.setLieu(demandeDTO.getLieu());
        demande.setDistrict(demandeDTO.getDistrict());
        demande.setClient(client);
        
        // 3. Sauvegarder la demande
        Demande savedDemande = demandeRepository.save(demande);
        
        // 4. Récupérer le statut par défaut (par exemple "Créé" avec id=1)
        Status defaultStatus = statusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Statut par défaut non trouvé"));
        
        // 5. Créer l'entrée dans demande_status
        DemandeStatus demandeStatus = new DemandeStatus();
        demandeStatus.setDemande(savedDemande);
        demandeStatus.setStatus(defaultStatus);
        demandeStatus.setDate(LocalDateTime.now());
        demandeStatus.setCommentaire("Demande créée");
        
        // 6. Sauvegarder le statut
        demandeStatusRepository.save(demandeStatus);
        
        // 7. Convertir en DTO et retourner
        DemandeDTO resultDTO = convertToDTO(savedDemande);
        resultDTO.setStatusLibelle(defaultStatus.getLibelle());
        
        return resultDTO;
    }
    
    @Override
    public DemandeDTO updateDemande(Long id, DemandeDTO demandeDTO) {
        Demande existingDemande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'id: " + id));
        
        existingDemande.setDate(demandeDTO.getDate());
        existingDemande.setLieu(demandeDTO.getLieu());
        existingDemande.setDistrict(demandeDTO.getDistrict());
        
        if (!existingDemande.getClient().getId().equals(demandeDTO.getClientId())) {
            Client newClient = clientRepository.findById(demandeDTO.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + demandeDTO.getClientId()));
            existingDemande.setClient(newClient);
        }
        
        Demande updatedDemande = demandeRepository.save(existingDemande);
        return convertToDTO(updatedDemande);
    }
    
    @Override
    public void deleteDemande(Long id) {
        if (!demandeRepository.existsById(id)) {
            throw new RuntimeException("Demande non trouvée avec l'id: " + id);
        }
        demandeRepository.deleteById(id);
    }
    
    @Override
    public List<DemandeDTO> findDemandesByClientId(Long clientId) {
        return demandeRepository.findByClientId(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DemandeDTO> searchDemandesByLieu(String lieu) {
        return demandeRepository.findByLieuContainingIgnoreCase(lieu).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private DemandeDTO convertToDTO(Demande demande) {
        DemandeDTO dto = new DemandeDTO();
        dto.setId(demande.getId());
        dto.setDate(demande.getDate());
        dto.setLieu(demande.getLieu());
        dto.setDistrict(demande.getDistrict());
        dto.setClientId(demande.getClient().getId());
        dto.setClientNom(demande.getClient().getNom());
        return dto;
    }
}