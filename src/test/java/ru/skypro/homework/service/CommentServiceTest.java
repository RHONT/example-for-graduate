package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.controller.CommentController;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.Role;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.CommentsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@AutoConfigureMockMvc
@SpringBootTest

class CommentServiceTest {

    @Mock
    CommentsRepository commentsRepository;

    @Mock
    private CommentsMapper commentsMapper;

    @InjectMocks
    CommentService commentService;

    private static final String testString = "Testing text";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    private static final UserEntity testUser = UserEntity.builder()
            .id(1)
            .firstName("Mad")
            .lastName("Hatter")
            .phone("123123123")
            .username("testUser")
            .build();

    private static final AdEntity adEntityTest = AdEntity.builder()
            .pk(12)
            .price(300)
            .title("The Hat")
            .description("old hat")
            .author(testUser)
            .build();

    private static final CommentEntity testCommentEntity = CommentEntity.builder()
            .text(testString)
            .adEntity(adEntityTest)
            .userEntity(testUser)
            .adEntity(adEntityTest)
            .commentId(123)
            .build();

//    @Test
//    void addNewCommentTest() {
//        Integer id = 1;
//        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
//        createOrUpdateComment.setText(testString);
//
//        CommentDto result = commentService.addNewComment(id, createOrUpdateComment);
//
//        assertEquals(1,result.getAuthor());
//        assertEquals(testString,result.getText());
//    }
//
//
//
//    @Test
//    void deleteComment() {
//        CommentEntity commentForDel = testCommentEntity;
//
//        commentForDel = commentService.deleteComment(12,123,mock(UserDetails));
//        assertEquals(null,commentForDel);
//    }
//
//    @Test
//    void updateComment() {
//
//    }
//
//    @Test
//    void getCommentsByAuthorId() {
//    }
}