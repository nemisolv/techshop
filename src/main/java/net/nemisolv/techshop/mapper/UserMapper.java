package net.nemisolv.techshop.mapper;

import net.nemisolv.techshop.entity.User;
import net.nemisolv.techshop.payload.user.FullUserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public FullUserInfo toFullUserInfo(User user) {
        return new FullUserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified(),
                user.getImgUrl(),
                user.getRole().getName(),
                user.getAuthProvider().name()
        );
    }
}
