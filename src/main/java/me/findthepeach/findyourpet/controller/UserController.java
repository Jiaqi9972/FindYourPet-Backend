package me.findthepeach.findyourpet.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.findyourpet.config.WebConstants;
import me.findthepeach.findyourpet.domain.dto.UpdateProfileDTO;
import me.findthepeach.findyourpet.domain.dto.UserDTO;
import me.findthepeach.findyourpet.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public UserDTO login(@RequestHeader(WebConstants.AUTHORIZATION_HEADER) String tokenHeader) {
        return userService.login(tokenHeader);
    }

    @PostMapping("/updateInfo")
    public void updateProfile(
            @RequestHeader(WebConstants.AUTHORIZATION_HEADER) String tokenHeader,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        userService.updateUserProfile(tokenHeader, updateProfileDTO);
    }
}