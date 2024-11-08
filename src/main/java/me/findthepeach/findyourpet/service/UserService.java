package me.findthepeach.findyourpet.service;

import me.findthepeach.findyourpet.domain.dto.UpdateProfileDTO;
import me.findthepeach.findyourpet.domain.dto.UserDTO;

public interface UserService {

    UserDTO login(String tokenHeader);

    void updateUserProfile(String tokenHeader, UpdateProfileDTO updateProfileDTO);
}
