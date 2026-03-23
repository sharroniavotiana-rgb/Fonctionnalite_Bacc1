package com.example.gestiondevis.service.impl;

import com.example.gestiondevis.dto.ClientDTO;
import com.example.gestiondevis.entity.Client;
import com.example.gestiondevis.repository.ClientRepository;
import com.example.gestiondevis.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    
    @Override
    public List<ClientDTO> findAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClientDTO findClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + id));
        return convertToDTO(client);
    }
    
    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {
        Client client = convertToEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return convertToDTO(savedClient);
    }
    
    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + id));
        
        existingClient.setNom(clientDTO.getNom());
        existingClient.setContact(clientDTO.getContact());
        
        Client updatedClient = clientRepository.save(existingClient);
        return convertToDTO(updatedClient);
    }
    
    @Override
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'id: " + id);
        }
        clientRepository.deleteById(id);
    }
    
    @Override
    public List<ClientDTO> searchClientsByNom(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setContact(client.getContact());
        return dto;
    }
    
    private Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setNom(dto.getNom());
        client.setContact(dto.getContact());
        return client;
    }
}