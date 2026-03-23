package com.example.gestiondevis.service;

import com.example.gestiondevis.dto.DemandeDTO;
import java.util.List;

public interface DemandeService {
    List<DemandeDTO> findAllDemandes();
    DemandeDTO findDemandeById(Long id);
    DemandeDTO saveDemande(DemandeDTO demandeDTO);
    DemandeDTO updateDemande(Long id, DemandeDTO demandeDTO);
    void deleteDemande(Long id);
    List<DemandeDTO> findDemandesByClientId(Long clientId);
    List<DemandeDTO> searchDemandesByLieu(String lieu);
}