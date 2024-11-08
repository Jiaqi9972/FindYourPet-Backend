package me.findthepeach.findyourpet.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.findyourpet.domain.dto.LostPetRequestDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetUpdateDTO;
import me.findthepeach.findyourpet.service.LostPetService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class LostPetController {

    private final LostPetService lostPetService;

    @PostMapping("/lost")
    public void saveLostPet(@RequestBody LostPetRequestDTO lostPetRequestDTO) {
        lostPetService.saveLostPet(lostPetRequestDTO);
    }

    @PutMapping("/update/{id}")
    public void updateLostPet(@PathVariable UUID id, @RequestBody LostPetUpdateDTO lostPetUpdateDTO) {
        lostPetService.updateLostPet(id, lostPetUpdateDTO);
    }

    // Renamed the endpoint and method
    @PutMapping("/completed/{id}")
    public void markAsCompleted(@PathVariable UUID id) {
        lostPetService.markAsCompleted(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLostPet(@PathVariable UUID id) {
        lostPetService.deleteLostPet(id);
    }
}
