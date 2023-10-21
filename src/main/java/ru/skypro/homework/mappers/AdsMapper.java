package ru.skypro.homework.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface AdsMapper {
    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);

    AdEntity updateAdDtoToAdEntity(CreateOrUpdateAdDto createOrUpdateAdDto);
    CreateOrUpdateAdDto adEntityToCrOrUpdAdDto (AdEntity adEntity);

    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "imageEntity.filePath", target = "image")
    AdDto adEntityToAdDto (AdEntity adEntity);

    @InheritInverseConfiguration
    AdEntity adDtoToEntity (AdDto adDto);


    @Mapping(target = "email", source = "author.username")
    @Mapping(target = "phone", source = "author.phone")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "image", source = "imageEntity.filePath")
    ExtendedAdDto adEntityToExAdDto (AdEntity adEntity);

    AdEntity adDtoToEntity (ExtendedAdDto extendedAdDto);


    List<AdDto> ListAdToListDto(List<AdEntity> list);
}
