package me.findthepeach.findyourpet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import me.findthepeach.findyourpet.domain.dto.LostPetDetailsDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetSummaryDTO;
import me.findthepeach.findyourpet.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
@Tag(name = "Search API", description = "APIs for searching lost and found pets")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/lost-pets/map")
    @Operation(summary = "Get summary information of pets for map display",
            description = "Returns a list of pets within the specified radius, optionally filtered by lost/found status")
    public List<LostPetSummaryDTO> searchLostPetsForMap(
            @Parameter(description = "Center latitude of search area")
            @RequestParam @DecimalMin("-90") @DecimalMax("90") Double latitude,
            @Parameter(description = "Center longitude of search area")
            @RequestParam @DecimalMin("-180") @DecimalMax("180") Double longitude,
            @Parameter(description = "Search radius in miles")
            @RequestParam @Positive Double radiusInMiles,
            @Parameter(description = "Filter by status: true for lost pets, false for found pets, null for both")
            @RequestParam(required = false) Boolean lost) {

        return searchService.searchLostPetsForMap(latitude, longitude, radiusInMiles, lost);
    }

    @GetMapping("/lost-pets/list")
    @Operation(summary = "Get detailed pet information with pagination",
            description = "Returns paginated detailed information of pets within the specified radius")
    public Page<LostPetDetailsDTO> searchLostPetsWithPagination(
            @Parameter(description = "Center latitude of search area")
            @RequestParam @DecimalMin("-90") @DecimalMax("90") Double latitude,
            @Parameter(description = "Center longitude of search area")
            @RequestParam @DecimalMin("-180") @DecimalMax("180") Double longitude,
            @Parameter(description = "Search radius in miles")
            @RequestParam @Positive Double radiusInMiles,
            @Parameter(description = "Page number (0-based)")
            @RequestParam @PositiveOrZero Integer page,
            @Parameter(description = "Page size")
            @RequestParam @Positive @Max(50) Integer size,
            @Parameter(description = "Filter by status: true for lost pets, false for found pets, null for both")
            @RequestParam(required = false) Boolean lost) {

        return searchService.searchLostPetsWithPagination(latitude, longitude, radiusInMiles, page, size, lost);
    }

    @GetMapping("/lost-pets/detail/{id}")
    @Operation(summary = "Get detailed information of a specific pet",
            description = "Returns detailed information for a pet by its ID")
    public LostPetDetailsDTO getLostPetDetail(
            @Parameter(description = "Pet ID")
            @PathVariable UUID id) {

        return searchService.getLostPetDetail(id);
    }
}