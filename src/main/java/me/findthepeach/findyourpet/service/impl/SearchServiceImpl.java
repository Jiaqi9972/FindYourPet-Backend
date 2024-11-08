package me.findthepeach.findyourpet.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;
import me.findthepeach.findyourpet.common.exception.BaseException;
import me.findthepeach.findyourpet.domain.dto.LostPetDetailsDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetSummaryDTO;
import me.findthepeach.findyourpet.domain.entity.LostPet;
import me.findthepeach.findyourpet.repository.LostPetRepository;
import me.findthepeach.findyourpet.service.SearchService;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final LostPetRepository lostPetRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LostPetSummaryDTO> searchLostPetsForMap(
            Double latitude,
            Double longitude,
            Double radiusInMiles,
            Boolean lost) {

        log.debug("Searching pets for map: lat={}, lon={}, radius={}, lost={}",
                latitude, longitude, radiusInMiles, lost);

        try {
            List<Object[]> results = lostPetRepository.findPetsNearbyForMap(longitude, latitude, radiusInMiles, lost);
            return results.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching pets for map", e);
            throw new BaseException(ReturnCode.RC500, "Failed to search pets for map");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LostPetDetailsDTO> searchLostPetsWithPagination(
            Double latitude,
            Double longitude,
            Double radiusInMiles,
            int page,
            int size,
            Boolean lost) {

        log.debug("Searching pets with pagination: lat={}, lon={}, radius={}, page={}, size={}, lost={}",
                latitude, longitude, radiusInMiles, page, size, lost);

        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Object[]> results = lostPetRepository.findPetsNearbyWithPagination(
                    longitude, latitude, radiusInMiles, lost, pageRequest);

            return results.map(this::mapToDetailsDTO);
        } catch (Exception e) {
            log.error("Error searching pets with pagination", e);
            throw new BaseException(ReturnCode.RC500, "Failed to search pets with pagination");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LostPetDetailsDTO getLostPetDetail(UUID id) {
        log.debug("Getting pet details for id: {}", id);

        try {
            LostPet lostPet = lostPetRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ReturnCode.RC404, "Pet not found with id: " + id));

            return mapToDetailsDTO(lostPet);
        } catch (BaseException e) {
            throw e;  // Rethrow BaseException directly
        } catch (Exception e) {
            log.error("Error getting pet details", e);
            throw new BaseException(ReturnCode.RC500, "Failed to get pet details");
        }
    }

    private LostPetSummaryDTO mapToSummaryDTO(Object[] result) {
        try {
            UUID id = (UUID) result[0];
            String name = (String) result[1];
            Point location = (Point) result[2];
            String petImageUrl = (String) result[3];
            Boolean isLost = (Boolean) result[4];

            G2D position = (G2D) location.getPosition();

            return LostPetSummaryDTO.builder()
                    .id(id)
                    .name(name)
                    .latitude(position.getLat())
                    .longitude(position.getLon())
                    .petImageUrl(petImageUrl)
                    .lost(isLost)
                    .build();
        } catch (Exception e) {
            log.error("Error mapping to SummaryDTO", e);
            throw new BaseException(ReturnCode.RC500, "Error mapping pet summary data");
        }
    }

    private LostPetDetailsDTO mapToDetailsDTO(Object[] result) {
        try {
            UUID id = (UUID) result[0];
            String name = (String) result[1];
            String description = (String) result[2];
            String posterContact = (String) result[3];
            Point location = (Point) result[4];
            Boolean isLost = (Boolean) result[5];
            String petImageUrlsJson = (String) result[6];
            String address = (String) result[8];

            List<String> petImageUrls = deserializeImageUrls(petImageUrlsJson);
            OffsetDateTime date = parseDateTime(result[7]);
            G2D position = (G2D) location.getPosition();

            return buildDetailsDTO(id, name, description, posterContact, position,
                    isLost, petImageUrls, date, address);
        } catch (Exception e) {
            log.error("Error mapping to DetailsDTO", e);
            throw new BaseException(ReturnCode.RC500, "Error mapping pet details data");
        }
    }

    private LostPetDetailsDTO mapToDetailsDTO(LostPet lostPet) {
        try {
            G2D position = (G2D) lostPet.getLocation().getPosition();
            return buildDetailsDTO(
                    lostPet.getId(),
                    lostPet.getName(),
                    lostPet.getDescription(),
                    lostPet.getPosterContact(),
                    position,
                    lostPet.getLost(),
                    lostPet.getPetImageUrls(),
                    lostPet.getDate(),
                    lostPet.getAddress()
            );
        } catch (Exception e) {
            log.error("Error mapping LostPet to DetailsDTO", e);
            throw new BaseException(ReturnCode.RC500, "Error mapping pet entity to DTO");
        }
    }

    private List<String> deserializeImageUrls(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Error deserializing image URLs", e);
            return Collections.emptyList();
        }
    }

    private OffsetDateTime parseDateTime(Object dateObj) {
        try {
            if (dateObj instanceof OffsetDateTime) {
                return (OffsetDateTime) dateObj;
            } else if (dateObj instanceof Timestamp) {
                return ((Timestamp) dateObj).toInstant().atOffset(ZoneOffset.UTC);
            } else if (dateObj instanceof Instant) {
                return ((Instant) dateObj).atOffset(ZoneOffset.UTC);
            }
            throw new BaseException(ReturnCode.RC500, "Unsupported date type: " + dateObj.getClass());
        } catch (Exception e) {
            log.error("Error parsing date time", e);
            throw new BaseException(ReturnCode.RC500, "Error parsing date time");
        }
    }

    private LostPetDetailsDTO buildDetailsDTO(
            UUID id, String name, String description, String posterContact,
            G2D position, Boolean isLost, List<String> petImageUrls,
            OffsetDateTime date, String address) {

        return LostPetDetailsDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .posterContact(posterContact)
                .latitude(position.getLat())
                .longitude(position.getLon())
                .address(address)
                .lost(isLost)
                .petImageUrls(petImageUrls)
                .date(date)
                .build();
    }
}