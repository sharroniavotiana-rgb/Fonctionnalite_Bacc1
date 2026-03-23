package com.example.gestiondevis.service;

import com.example.gestiondevis.dto.ClientDTO;
import java.util.List;

public interface ClientService {
    List<ClientDTO> findAllClients();
    ClientDTO findClientById(Long id);
    ClientDTO saveClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
    List<ClientDTO> searchClientsByNom(String nom);
}