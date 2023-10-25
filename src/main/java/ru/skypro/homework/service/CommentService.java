package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.exceptions.NoAdException;
import ru.skypro.homework.exceptions.UnauthorizedException;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;

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
    private final CommentsMapper commentsMapper;
    private final AdsRepository adsRepository;

    /**
     * Добавляем комментарий к объявлению
     * @param id
     * @param сreateOrUpdateComment
     * @return
     */
    public CommentDto addNewComment(Integer id, CreateOrUpdateComment сreateOrUpdateComment) {
        AdEntity ad = adsRepository.findById(id).get();

        CommentEntity comment = CommentEntity.builder().
                createdAt(Instant.now().toEpochMilli()).
                text(сreateOrUpdateComment.getText()).
                adEntity(ad).
                userEntity(ad.getAuthor()).build();
        commentsRepository.save(comment);
        return commentsMapper.commentEntityToCommentDto(comment);
    }

    /**
     * Удаление комментария
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
     * @param commentId - комментария
     * @param text      - суть комментария
     * @return
     */
    public CommentDto updateComment(Integer commentId, UserDetails userDetails, String text) {
        Optional<CommentEntity> commentEntity = commentsRepository.findById(commentId);
        if (commentEntity.isPresent()) {
            checkAuthority(userDetails, commentEntity.get());
            commentEntity.get().setText(text);
            commentsRepository.save(commentEntity.get());
        } else {
            log.debug("Comment with id={} not found", commentId);
            throw new NoAdException("Comment with id = " + commentId + "not found");
        }
        return commentsMapper.commentEntityToCommentDto(commentEntity.get());
    }

    /**
     * Получить все комментарии в объявлении
     * @param id - объявления
     * @return
     */
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

    /**
     * Проверка является ли комментарий личным
     */
    private boolean itISUserComment(UserDetails userDetails, CommentEntity comment) {
        return Objects.equals(userDetails.getUsername(), comment.getUserEntity().getUsername());
    }

    /**
     * Если авторизованный пользователь админ, то он имеет доступ на корректировку любого комментария
     * @param userDetails
     * @return
     */
    private boolean userIsAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    /**
     * Аккумулированный метод использующий userIsAdmin() и itISUserComment(), и если все плохо кидаем
     * исключение и пишем в лог событие
     * @param userDetails
     * @param comment
     */
    private void checkAuthority(UserDetails userDetails, CommentEntity comment) {
        if (!itISUserComment(userDetails, comment) && !userIsAdmin(userDetails)) {
            log.debug("Attempted unauthorized access id comment={}", comment.getCommentId());
            throw new UnauthorizedException("Attempted unauthorized access");
        }
    }
}
