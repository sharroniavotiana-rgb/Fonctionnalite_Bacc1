package com.example.gestiondevis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDTO {
    
    private Long id;
    
    @NotNull(message = "La date est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @NotBlank(message = "Le lieu est obligatoire")
    @Size(max = 255, message = "Le lieu ne peut pas dépasser 255 caractères")
    private String lieu;
    
    @NotBlank(message = "Le district est obligatoire")
    @Size(max = 255, message = "Le district ne peut pas dépasser 255 caractères")
    private String district;
    
    @NotNull(message = "Le client est obligatoire")
    private Long clientId;
    
    private String clientNom;
}