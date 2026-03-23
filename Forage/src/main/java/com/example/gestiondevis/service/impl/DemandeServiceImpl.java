package com.example.gestiondevis.service.impl;

import com.example.gestiondevis.dto.DemandeDTO;
import com.example.gestiondevis.entity.Client;
import com.example.gestiondevis.entity.Demande;
import com.example.gestiondevis.repository.ClientRepository;
import com.example.gestiondevis.repository.DemandeRepository;
import com.example.gestiondevis.service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeServiceImpl implements DemandeService {
    
    private final DemandeRepository demandeRepository;
    private final ClientRepository clientRepository;
    
    @Override
    public List<DemandeDTO> findAllDemandes() {
        return demandeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DemandeDTO findDemandeById(Long id) {
        Demande demande = demandeRepository.findByIdWithClient(id);
        if (demande == null) {
            throw new RuntimeException("Demande non trouvée avec l'id: " + id);
        }
        return convertToDTO(demande);
    }
    
    @Override
    public DemandeDTO saveDemande(DemandeDTO demandeDTO) {
        Client client = clientRepository.findById(demandeDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + demandeDTO.getClientId()));
        
        Demande demande = convertToEntity(demandeDTO, client);
        Demande savedDemande = demandeRepository.save(demande);
        return convertToDTO(savedDemande);
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
    
    private Demande convertToEntity(DemandeDTO dto, Client client) {
        Demande demande = new Demande();
        demande.setId(dto.getId());
        demande.setDate(dto.getDate());
        demande.setLieu(dto.getLieu());
        demande.setDistrict(dto.getDistrict());
        demande.setClient(client);
        return demande;
    }
}