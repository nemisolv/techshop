package net.nemisolv.techshop.security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nemisolv.techshop.entity.Role;
import net.nemisolv.techshop.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String imgUrl;
    private boolean active;
    private boolean emailVerified;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;


    public static UserPrincipal create(User user) {

        List<SimpleGrantedAuthority> authorities = buildAuthorities(user.getRole());

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .emailVerified(user.isEmailVerified())
                .imgUrl(user.getImgUrl())
                .active(user.isActive())
                .authorities(authorities)
                .build();
    }

    private static List<SimpleGrantedAuthority> buildAuthorities(Role role) {
        var authorities = new java.util.ArrayList<>(role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        return authorities;
    }

    public static UserPrincipal create(User user, Map<String,Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }



}