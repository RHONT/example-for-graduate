package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.CommentsRepository;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final CommentsMapper commentsMapper;

    public CommentDto addNewComment(long id, String text) {
        CommentDto commentDto = new CommentDto();
        Integer newId = Math.toIntExact(id);
        commentDto.setText(text);
        commentDto.setCreatedAt(newId);
        commentsRepository.save(commentsMapper.commentDtoTocommentEntity(commentDto));
        return commentDto;
    }


    public void deleteComment(Integer adId, Integer commentId) {
        commentsRepository.deleteByCreatedAtAndCommentId(adId, commentId);
    }

    public CommentDto updateComment(Integer adId, Integer commentId, String text) {
        CommentEntity comment = commentsRepository.findCommentEntitiesByAuthorAfterAndCommentId(adId,commentId);
        comment.setText(text);
        commentsRepository.save(comment);
        return commentsMapper.commentEntityToCommentDto(comment);
    }

    public CommentsDto getCommentsByAuthorId(Integer id) {
        ArrayList<CommentEntity> commentsList = commentsRepository.findCommentEntitiesByAuthor(id);
        ArrayList<CommentDto> commentDtos = new ArrayList<>();
        commentsList.forEach(commentEntity -> {
            CommentDto commentDto = commentsMapper.commentEntityToCommentDto(commentEntity);
            commentDtos.add(commentDto);
        });
        CommentsDto comments = new CommentsDto();
        comments.setResults(commentDtos);
        comments.setCount(commentDtos.size());
        return comments;
    }
}
