package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.CommentEntity;

@Repository
public interface CommentsRepository extends JpaRepository <CommentEntity, Long> {
}
