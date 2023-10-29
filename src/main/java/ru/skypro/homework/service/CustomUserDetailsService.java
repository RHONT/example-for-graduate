package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.LoginDtoInterface;
import ru.skypro.homework.dto.RolesDtoInterface;
import ru.skypro.homework.dto.UserMinimalDataDto;
import ru.skypro.homework.entities.Role;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repository.UsersRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<LoginDtoInterface> userEntity = usersRepository.findLoginAndPasswordByUserName(username);
        List<RolesDtoInterface> rolesEntity = usersRepository.findRolesByUserName(username);

        if (userEntity.isEmpty()) {
            log.debug("User with name {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                userEntity.get().getUsername(),
                userEntity.get().getPassword(),
                collectionRolesToAuth(rolesEntity));
    }

    private Collection<GrantedAuthority> collectionRolesToAuth(List<RolesDtoInterface> roleList){
        return roleList.stream().map(el->new SimpleGrantedAuthority("ROLE_"+el.getName())).collect(Collectors.toList());
    }
}
