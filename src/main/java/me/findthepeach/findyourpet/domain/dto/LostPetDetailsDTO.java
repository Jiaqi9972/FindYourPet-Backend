package me.findthepeach.findyourpet.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class LostPetDetailsDTO {
    private UUID id;
    private String name;
    private String description;
    private String posterContact;
    private double longitude;
    private double latitude;
    private Boolean lost;
    private String address;
    private List<String> petImageUrls;
    private OffsetDateTime date;
}