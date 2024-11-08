package me.findthepeach.findyourpet.repository;

import me.findthepeach.findyourpet.domain.entity.LostPet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LostPetRepository extends JpaRepository<LostPet, UUID> {

    // search summary for map (only incomplete lost pets)
    @Query(value = "SELECT p.pet_id, p.name, p.location, " +
            "CASE WHEN jsonb_array_length(p.pet_image_urls) > 0 THEN p.pet_image_urls->>0 ELSE NULL END AS petImageUrl, " +
            "p.lost " +
            "FROM lost_pets p " +
            "WHERE ST_DWithin(p.location::geography, ST_MakePoint(?1, ?2)::geography, ?3 * 1609.34) " +
            "AND p.completed = false " +
            "AND (?4 IS NULL OR p.lost = ?4)",
            nativeQuery = true)
    List<Object[]> findPetsNearbyForMap(Double longitude, Double latitude, double radiusInMiles, Boolean lost);

    // search details for page (only incomplete lost pets with pagination)
    @Query(value = "SELECT p.pet_id, p.name, p.description, p.poster_contact, p.location, " +
            "p.lost, p.pet_image_urls, p.date, p.address " +
            "FROM lost_pets p " +
            "WHERE ST_DWithin(p.location::geography, ST_MakePoint(?1, ?2)::geography, ?3 * 1609.34) " +
            "AND p.completed = false " +
            "AND (?4 IS NULL OR p.lost = ?4)",
            nativeQuery = true)
    Page<Object[]> findPetsNearbyWithPagination(Double longitude, Double latitude, double radiusInMiles, Boolean lost, Pageable pageable);
}
