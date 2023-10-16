package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.UserEntity;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userEntityToUserDto(UserEntity userEntity);
    UserEntity userDtoToUserEntity (UserDto userDto);
    UserEntity registerDtoToUserEntity (RegisterDto registerDto);
    UserEntity updateByUpdateUserDTO(UpdateUserDto updateUserDto, @MappingTarget UserEntity userEntity);
    UpdateUserDto userEntityToUpdateUserDTo(UserEntity userEntity);
}
