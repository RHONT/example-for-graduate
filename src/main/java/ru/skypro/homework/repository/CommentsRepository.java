package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.CommentEntity;

import java.util.ArrayList;

@Repository
public interface CommentsRepository extends JpaRepository <CommentEntity, Integer> {
    void deleteByCreatedAtAndCommentId (Integer createdAt, Integer commentId);
    CommentEntity findCommentEntitiesByAuthorAfterAndCommentId (Integer createdAt, Integer commentId);
    ArrayList<CommentEntity> findCommentEntitiesByAuthor (Integer id);
}
