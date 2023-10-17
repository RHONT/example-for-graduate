package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;

@Component
@Mapper(componentModel = "spring")
public interface AdsMapper {
    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);

    AdEntity crOrUpdAdDtoToAdEntity (CreateOrUpdateAdDto createOrUpdateAdDto);
    CreateOrUpdateAdDto adEntityToCrOrUpdAdDto (AdEntity adEntity);
    AdDto adEntityToAdDto (AdEntity adEntity);
    AdEntity adDtoToEntity (AdDto adDto);
    ExtendedAdDto adEntityToExAdDto (AdEntity adEntity);
    AdEntity adDtoToEntity (ExtendedAdDto extendedAdDto);
}
