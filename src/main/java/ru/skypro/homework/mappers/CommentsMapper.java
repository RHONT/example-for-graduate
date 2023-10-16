package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.CommentEntity;

@Component
@Mapper(componentModel = "spring")
public interface CommentsMapper {
    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);


    CommentEntity commentDtoTocommentEntity (CommentDto commentDto);

    CommentDto commentEntityToCommentDto (CommentEntity commentEntity);

}
