package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entities.CommentEntity;

@Mapper
public interface CommentsMapper {
    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);


    CommentEntity commentDtoTocommentEntity (CommentDto commentDto);

    CommentDto commentEntityToCommentDto (CommentEntity commentEntity);

}
