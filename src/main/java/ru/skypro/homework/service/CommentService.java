package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final CommentsMapper commentsMapper;
    private final UsersRepository usersRepository;

    public CommentDto addNewComment(long id, String text) {
        CommentDto commentDto = new CommentDto();
        Integer newId = Math.toIntExact(id);
        commentDto.setText(text);
        commentDto.setCreatedAt(newId);
        return commentDto;
    }


}
