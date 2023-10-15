package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.UserEntity;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userEntityToUserDto(UserEntity userEntity);
    UserEntity userDtoToUserEntity (UserDto userDto);
    UserEntity registerDtoToUserEntity (RegisterDto registerDto);
}
