package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository <CommentEntity, Integer> {
    void deleteByCommentIdAndAdEntity_Pk (Integer commentId,Integer adId );
}
