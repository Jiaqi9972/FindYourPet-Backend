package me.findthepeach.findyourpet.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import me.findthepeach.findyourpet.common.constant.template.ReturnCode;
import me.findthepeach.findyourpet.common.exception.BaseException;
import me.findthepeach.findyourpet.domain.dto.UpdateProfileDTO;
import me.findthepeach.findyourpet.domain.dto.UserDTO;
import me.findthepeach.findyourpet.domain.entity.User;
import me.findthepeach.findyourpet.repository.UserRepository;
import me.findthepeach.findyourpet.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;

    public UserDTO login(String tokenHeader) {
        try {
            // 从 header 中获取 token
            String idToken = tokenHeader.replace("Bearer ", "");
            FirebaseToken token = firebaseAuth.verifyIdToken(idToken);

            // 使用 Firebase 的 UID 来查找用户
            String firebaseUid = token.getUid();
            String email = token.getEmail();

            // 从数据库查找用户
            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseGet(() -> {
                        // 如果用户不存在，则创建新用户并保存到数据库
                        User newUser = User.builder()
                                .firebaseUid(firebaseUid)
                                .email(email)
                                .build();
                        return userRepository.save(newUser);
                    });

            // 检查用户的 name 和 avatarUrl 是否为空
            boolean isProfileComplete = user.getName() != null && !user.getName().isEmpty()
                    && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty();

            // 返回 UserDTO，包含 isProfileComplete 标志
            return UserDTO.builder()
                    .id(user.getId())
                    .firebaseUid(user.getFirebaseUid())
                    .email(user.getEmail())
                    .name(user.getName())
                    .avatarUrl(user.getAvatarUrl())
                    .isProfileComplete(isProfileComplete)
                    .build();

        } catch (FirebaseAuthException e) {
            throw new BaseException(ReturnCode.RC401, "Invalid Firebase ID Token");
        }
    }

    @Override
    @Transactional
    public void updateUserProfile(String tokenHeader, UpdateProfileDTO updateProfileDTO) {
        try {
            // 从 header 中获取 token
            String idToken = tokenHeader.replace("Bearer ", "");
            FirebaseToken token = firebaseAuth.verifyIdToken(idToken);

            // 获取 Firebase UID
            String firebaseUid = token.getUid();

            // 根据 Firebase UID 查找用户
            User user = userRepository.findByFirebaseUid(firebaseUid)
                    .orElseThrow(() -> new BaseException(ReturnCode.RC404, "User not found"));

            // 更新用户信息
            user.setName(updateProfileDTO.getName());
            user.setAvatarUrl(updateProfileDTO.getAvatarUrl());

            // 保存更新后的用户信息
            userRepository.save(user);

        } catch (FirebaseAuthException e) {
            throw new BaseException(ReturnCode.RC401, "Invalid Firebase ID Token");
        }
    }

}
