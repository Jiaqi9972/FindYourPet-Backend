package me.findthepeach.findyourpet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String firebaseUid;
    private String email;
    private String name;
    private String avatarUrl;
    private boolean isProfileComplete;
}
