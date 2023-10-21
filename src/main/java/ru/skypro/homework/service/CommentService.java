package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.exceptions.NoCommentException;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final CommentsMapper commentsMapper;
    private final AdsRepository adsRepository;

    /**
     * Добавляем комментарий к объявлению
     *
     * @param id
     * @param text
     * @return
     */
    public CommentDto addNewComment(Integer id, String text) {
        AdEntity ad = adsRepository.findById(id).get();

        CommentEntity comment = CommentEntity.builder().
                createdAt(Instant.now().toEpochMilli()).
                text(text).
                adEntity(ad).
                userEntity(ad.getAuthor()).build();
        commentsRepository.save(comment);
        return commentsMapper.commentEntityToCommentDto(comment);
    }


    /**
     * Удаление комментария
     *
     * @param adId      - id объявления
     * @param commentId - id комментария
     */
    @Transactional
    public void deleteComment(Integer adId, Integer commentId) {
        commentsRepository.deleteByCommentIdAndAdEntity_Pk(commentId, adId);
    }

    //todo Зачем нам работать с adId, если мы можем сразу работать с commentId?

    /**
     * Обновления комментария
     *
     * @param adId      - объявления
     * @param commentId - комментария
     * @param text      - суть комментария
     * @return
     */
    public CommentDto updateComment(Integer adId, Integer commentId, UserDetails userDetails, String text) {
        Optional<AdEntity> adEntity = adsRepository.findById(adId);
        Optional<CommentEntity> commentEntity = Optional.of(new CommentEntity());

        if (adEntity.isPresent()) {
            if (Objects.equals(userDetails.getUsername(), adEntity.get().getAuthor().getUsername()) ||
                    userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                commentEntity = commentsRepository.findById(commentId);
                if (commentEntity.isPresent()) {
                    commentEntity.get().setText(text);
                    commentsRepository.save(commentEntity.get());
                } else {
                    log.debug("Comment with id={} not found", commentId);
                    throw new NoCommentException("Объявления с номером " + adId + "не существует");
                }
            } else {
                log.debug("Attempted unauthorized access idAd={}, idComment{}", adId, commentId);
                //todo Почему-то все рушиться от этого эксепшена
//                throw new UnauthorizedException("Attempted unauthorized access");
            }
        } else {
            log.debug("Ad with id={} not found", adId);
            throw new NoAdException("Объявления с номером " + adId + "не существует");
        }

        return commentsMapper.commentEntityToCommentDto(commentEntity.get());
    }

    @Transactional
    public CommentsDto getCommentsByIdAd(Integer id) {
        AdEntity ad = adsRepository.findById(id).get();
        CommentsDto comments = new CommentsDto();

        List<CommentEntity> commentsList = ad.getCommentEntityList();

        ArrayList<CommentDto> commentsDto = (ArrayList<CommentDto>) commentsMapper.listCommentToListCommentDto(commentsList);
        comments.setCount(commentsDto.size());
        comments.setResults(commentsDto);
        return comments;
    }
}
