package me.findthepeach.findyourpet.service;

import me.findthepeach.findyourpet.domain.dto.LostPetDetailsDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetSummaryDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SearchService {

    List<LostPetSummaryDTO> searchLostPetsForMap(Double latitude, Double longitude, Double radiusInMiles, Boolean lost);

    Page<LostPetDetailsDTO> searchLostPetsWithPagination(Double latitude, Double longitude, Double radiusInMiles, int page, int size, Boolean lost);

    LostPetDetailsDTO getLostPetDetail(UUID id);
}
