package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Transactional
    public void uploadAvatar(MultipartFile file, Long idUser) throws IOException {
        log.info("method uploadAvatar is run");

        UserEntity userEntity =usersRepository.findById(idUser).get();
//        Image image=new Image();
//        image.setData(file.getBytes());
//        image.setFileSize(file.getSize());
//        image.setMediaType(file.getContentType());

        userEntity.setImage(file.getBytes());
        usersRepository.save(userEntity);
    }

    public UpdateUserDto updateInfoUser(UpdateUserDto updateUserDto, Authentication authentication) {
        UserEntity user=usersRepository.findByUsername(authentication.getName()).get();
        user=userMapper.updateByUpdateUserDTO(updateUserDto,user);
        usersRepository.save(user);
        return userMapper.userEntityToUpdateUserDTo(user);

    }

    public UserDto getInfoAboutUser(Authentication authentication) {
        UserEntity user=usersRepository.findByUsername(authentication.getName()).get();
        return userMapper.userEntityToUserDto(user);
    }
}
