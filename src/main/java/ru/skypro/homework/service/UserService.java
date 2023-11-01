package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.SetPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.ImageEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.utilclass.JavaFileToMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;

    @Transactional
    public void uploadAvatar(MultipartFile file, UserDetails userDetails) throws IOException {
        log.info("method uploadAvatar is run");

        if (file==null) {
            file=  new JavaFileToMultipartFile(new File("src/main/resources/image/test.jpg"));
        }
        UserEntity userEntity = usersRepository.findByUsername(userDetails.getUsername()).get();
        ImageEntity image = imageService.createImageEntity(file);
        userEntity.setImageEntity(image);
        usersRepository.save(userEntity);
        image.setFilePath(image.getFilePath() + userEntity.getImageEntity().getId().toString());
        imageRepository.save(image);
    }

    @Transactional
    public UpdateUserDto updateInfoUser(UpdateUserDto updateUserDto, UserDetails userDetails) {
        UserEntity user = usersRepository.findByUsername(userDetails.getUsername()).get();
        user = userMapper.updateByUpdateUserDTO(updateUserDto, user);
        usersRepository.save(user);
        return userMapper.userEntityToUpdateUserDTo(user);

    }

    @Transactional
    public UserDto getInfoAboutUser(UserDetails userDetails) {
        UserEntity user = usersRepository.findByUsername(userDetails.getUsername()).get();
        return userMapper.userEntityToUserDto(user);
    }

    @Transactional
    public SetPasswordDto setPassword(SetPasswordDto setPasswordDto, UserDetails userDetails) {
        Optional<UserEntity> user = usersRepository.findByUsername(userDetails.getUsername());
        setPasswordDto.setCurrentPassword(userDetails.getPassword());
        user.ifPresent(userEntity -> {
            userEntity.setPassword(passwordEncoder.encode(setPasswordDto.getNewPassword()));
            usersRepository.save(userEntity);
            setPasswordDto.setNewPassword(userEntity.getPassword());
        });
        return setPasswordDto;
    }
}
