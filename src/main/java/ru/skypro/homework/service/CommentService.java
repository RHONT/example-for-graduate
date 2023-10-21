package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final CommentsMapper commentsMapper;
    private final AdsRepository adsRepository;

    public CommentDto addNewComment(Integer id, String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        commentDto.setCreatedAt(id);
        commentsRepository.save(commentsMapper.commentDtoTocommentEntity(commentDto));
        return commentDto;
    }


    public void deleteComment(Integer adId, Integer commentId) {
        commentsRepository.deleteByCreatedAtAndCommentId(adId, commentId);
    }

    public CommentDto updateComment(Integer adId, Integer commentId, String text)  {

        Optional<AdEntity> adEntity= adsRepository.findById(adId);
        Optional<CommentEntity>  commentEntity = Optional.of(new CommentEntity());
        if (adEntity.isPresent()) {
            commentEntity=commentsRepository.findById(commentId);
            if (commentEntity.isPresent()) {
                commentEntity.get().setText(text);
                commentsRepository.save(commentEntity.get());
            }
        } else throw new NoAdException("Объявления с номером " + adId + "не существует");

        return commentsMapper.commentEntityToCommentDto(commentEntity.get());
    }

    public CommentsDto getCommentsByAuthorId(Integer id) {
        UserEntity user=usersRepository.findById(id).get();
        ArrayList<CommentEntity> commentsList = commentsRepository.findCommentEntitiesByUserEntity(user);
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
