package ru.skypro.homework.secutity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.UserMinimalDataDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repository.UsersRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserEntity> userEntity = usersRepository.findByUsername(username);

        if (userEntity.isEmpty()) {
            log.debug("User with name {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }
        UserEntity user=userEntity.get();
        UserMinimalDataDto userMinimalDataDto=new UserMinimalDataDto();
        userMinimalDataDto.setUsername(user.getUsername());
        userMinimalDataDto.setPassword(user.getPassword());
        userMinimalDataDto.setRoles(user.getRoles());

        return new MyPrincipal(userMinimalDataDto);
    }


}
