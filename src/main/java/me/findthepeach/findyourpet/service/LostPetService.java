package me.findthepeach.findyourpet.service;

import me.findthepeach.findyourpet.domain.dto.LostPetRequestDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetUpdateDTO;
import org.geolatte.geom.Point;

import java.util.UUID;

public interface LostPetService {
    void saveLostPet(LostPetRequestDTO lostPetRequestDTO);

    void updateLostPet(UUID id, LostPetUpdateDTO lostPetUpdateDTO);

    void markAsCompleted(UUID id);

    void deleteLostPet(UUID id);
}
