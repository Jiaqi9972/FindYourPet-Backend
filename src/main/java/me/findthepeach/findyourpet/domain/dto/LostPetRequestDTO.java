package me.findthepeach.findyourpet.domain.dto;


import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class LostPetRequestDTO {
    private String name;
    private String description;
    private String posterContact;
    private double longitude;
    private double latitude;
    private Boolean lost;
    private List<String> petImageUrls;
    private OffsetDateTime date;
}