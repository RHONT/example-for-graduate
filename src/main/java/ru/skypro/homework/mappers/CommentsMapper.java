package ru.skypro.homework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entities.CommentEntity;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CommentsMapper {
    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);


    CommentEntity commentDtoTocommentEntity (CommentDto commentDto);

    @Mapping(target = "author", source = "userEntity.id")
    @Mapping(target = "authorImage", source = "userEntity.imageEntity.filePath")
    @Mapping(target = "authorFirstName", source = "userEntity.firstName")
    @Mapping(target = "pk", source = "commentId")
    CommentDto commentEntityToCommentDto (CommentEntity commentEntity);

    List<CommentDto> listCommentToListCommentDto(List<CommentEntity> commentEntityList);

    CreateOrUpdateComment commEntityToCrOrUpdComment (CommentEntity commentEntity);
    CommentEntity crOrUpdCommentToCommEntity (CreateOrUpdateComment createOrUpdateComment);

}
