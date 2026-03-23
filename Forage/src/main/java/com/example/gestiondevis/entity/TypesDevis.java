package com.example.gestiondevis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "types_devis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypesDevis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;
    
    @OneToMany(mappedBy = "typeDevis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Devis> devis = new ArrayList<>();
}