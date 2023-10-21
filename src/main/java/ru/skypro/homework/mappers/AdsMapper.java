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


    ExtendedAdDto adEntityToExAdDto (AdEntity adEntity);

    AdEntity adDtoToEntity (ExtendedAdDto extendedAdDto);


    List<AdDto> ListAdToListDto(List<AdEntity> list);
}
