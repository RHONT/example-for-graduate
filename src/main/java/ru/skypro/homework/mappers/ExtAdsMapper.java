package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;

@Mapper
public interface ExtAdsMapper {

    ExtAdsMapper INSTANCE = Mappers.getMapper(ExtAdsMapper.class);

    ExtendedAdDto adEntityToAdsDto (AdEntity adEntity);

    AdEntity adDtoToEntity (ExtendedAdDto extendedAdDto);
}
