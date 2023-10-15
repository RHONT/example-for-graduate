package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.Mapping;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.entities.AdEntity;

@Mapper
public interface AdsMapper {
    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);


    AdDto adEntityToAdsDto (AdEntity adEntity);

    AdEntity adDtoToEntity (AdDto adDto);
}
