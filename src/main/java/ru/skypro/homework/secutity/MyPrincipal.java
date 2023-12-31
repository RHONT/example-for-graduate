package ru.skypro.homework.secutity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.UserMinimalDataDto;
import ru.skypro.homework.entities.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MyPrincipal implements UserDetails {
    private UserMinimalDataDto userMinimalDataDto;

    public MyPrincipal(UserMinimalDataDto userMinimalDataDto) {
        this.userMinimalDataDto = userMinimalDataDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role: userMinimalDataDto.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return userMinimalDataDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userMinimalDataDto.getUsername();
    }

    public UserMinimalDataDto getUserMinimalDataDto(){
        return this.userMinimalDataDto;
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
        return true;
    }

}
