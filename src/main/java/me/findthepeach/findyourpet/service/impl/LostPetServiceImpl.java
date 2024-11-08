package me.findthepeach.findyourpet.service.impl;

import lombok.RequiredArgsConstructor;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;
import me.findthepeach.findyourpet.common.exception.BaseException;
import me.findthepeach.findyourpet.domain.dto.LostPetRequestDTO;
import me.findthepeach.findyourpet.domain.dto.LostPetUpdateDTO;
import me.findthepeach.findyourpet.domain.entity.LostPet;
import me.findthepeach.findyourpet.domain.entity.User;
import me.findthepeach.findyourpet.repository.LostPetRepository;
import me.findthepeach.findyourpet.repository.UserRepository;
import me.findthepeach.findyourpet.service.LostPetService;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LostPetServiceImpl implements LostPetService {

    private final LostPetRepository lostPetRepository;
    private final UserRepository userRepository;
    private final GeocodingServiceImpl geocodingService;

    private static final Logger log = LoggerFactory.getLogger(LostPetServiceImpl.class);

    @Override
    @Transactional
    public void saveLostPet(LostPetRequestDTO lostPetRequestDTO) {
        try {
            String firebaseUid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseThrow(() -> new BaseException(ReturnCode.RC400));

            // 创建 location
            Point<G2D> location = Geometries.mkPoint(
                    new G2D(lostPetRequestDTO.getLongitude(), lostPetRequestDTO.getLatitude()),
                    CoordinateReferenceSystems.WGS84
            );

            // 获取地址
            String address = geocodingService.calculateAddressFromCoordinates(lostPetRequestDTO.getLatitude(), lostPetRequestDTO.getLongitude());
            log.info("Calculated address: {}", address); // 添加日志

            LostPet lostPet = LostPet.builder()
                    .name(lostPetRequestDTO.getName())
                    .description(lostPetRequestDTO.getDescription())
                    .posterContact(lostPetRequestDTO.getPosterContact())
                    .location(location)
                    .address(address) // 设置计算得到的地址
                    .petImageUrls(lostPetRequestDTO.getPetImageUrls())
                    .lost(lostPetRequestDTO.getLost())
                    .date(lostPetRequestDTO.getDate())
                    .createdOn(OffsetDateTime.now())
                    .editedOn(OffsetDateTime.now())
                    .completed(false)
                    .user(user)
                    .build();

            lostPetRepository.save(lostPet);
        } catch (Exception e) {
            log.error("Error saving lost pet: {}", e.getMessage(), e);
            throw new BaseException(ReturnCode.RC500);
        }
    }


    @Override
    @Transactional
    public void updateLostPet(UUID id, LostPetUpdateDTO lostPetUpdateDTO) {

        String firebaseUid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        LostPet lostPet = lostPetRepository.findById(id)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        if (!lostPet.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException(ReturnCode.RC403, "You are not authorized to update this pet.");
        }

        lostPet.setName(lostPetUpdateDTO.getName());
        lostPet.setDescription(lostPetUpdateDTO.getDescription());
        lostPet.setPosterContact(lostPetUpdateDTO.getPosterContact());

        // 在 service 中创建 location
        Point<G2D> newLocation = Geometries.mkPoint(
                new G2D(lostPetUpdateDTO.getLongitude(), lostPetUpdateDTO.getLatitude()),
                CoordinateReferenceSystems.WGS84
        );

        G2D oldCoordinates = lostPet.getLocation().getPosition();
        double oldLatitude = oldCoordinates.getLat();
        double oldLongitude = oldCoordinates.getLon();

        double newLatitude = lostPetUpdateDTO.getLatitude();
        double newLongitude = lostPetUpdateDTO.getLongitude();

        if (oldLatitude != newLatitude || oldLongitude != newLongitude) {
            lostPet.setLocation(newLocation);
            String newAddress = geocodingService.calculateAddressFromLocation(newLocation);
            lostPet.setAddress(newAddress);
        }

        lostPet.setLost(lostPetUpdateDTO.getLost());
        lostPet.setPetImageUrls(lostPetUpdateDTO.getPetImageUrls());
        lostPet.setDate(lostPetUpdateDTO.getDate());
        lostPet.setEditedOn(OffsetDateTime.now());

        lostPetRepository.save(lostPet);
    }

    @Override
    @Transactional
    public void markAsCompleted(UUID id) {
        String firebaseUid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        LostPet lostPet = lostPetRepository.findById(id)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        if (!lostPet.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException(ReturnCode.RC403, "You are not authorized to mark this pet as completed.");
        }

        lostPet.setCompleted(true);
        lostPet.setEditedOn(OffsetDateTime.now());

        lostPetRepository.save(lostPet);
    }

    @Override
    @Transactional
    public void deleteLostPet(UUID id) {
        String firebaseUid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        LostPet lostPet = lostPetRepository.findById(id)
                .orElseThrow(() -> new BaseException(ReturnCode.RC400));

        if (!lostPet.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException(ReturnCode.RC403, "You are not authorized to delete this pet.");
        }

        lostPetRepository.delete(lostPet);
    }
}

