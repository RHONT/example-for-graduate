package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageDto;
import ru.skypro.homework.entities.ImageEntity;

import java.io.IOException;

@Component
@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    ImageDto ImageEntityToImageDto(ImageEntity imageEntity);

    ImageEntity ImageDtoToImageEntity(ImageDto imageDto);

//    @Mapping(target = "data",expression = "java(file.getBytes())")
    @Mapping(target = "fileSize",expression = "java(file.getSize())")
    @Mapping(target = "mediaType",expression = "java(file.getContentType())")
    ImageEntity updateImageEntityFromFile(MultipartFile file, @MappingTarget ImageEntity imageEntity) throws IOException;
}
