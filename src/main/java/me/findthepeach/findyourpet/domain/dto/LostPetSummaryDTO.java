package me.findthepeach.findyourpet.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LostPetSummaryDTO {
    private UUID id;
    private String name;
    private double longitude;
    private double latitude;
    private String petImageUrl;
    private Boolean lost;
}