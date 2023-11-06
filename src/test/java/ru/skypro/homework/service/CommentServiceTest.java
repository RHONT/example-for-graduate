package ru.skypro.homework.service;

import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;

import ru.skypro.homework.entities.Role;
import ru.skypro.homework.entities.UserEntity;

import ru.skypro.homework.mappers.CommentsMapperImpl;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentsRepository commentsRepository;
    @Mock
    AdsRepository adsRepository;
    @Mock
    UsersRepository usersRepository;

    CommentService commentService;

    private static final String testString = "Testing text";
    private CommentEntity testCommentEntity;
    private AdEntity adEntityTest;
    private UserEntity testUser;

    CommentServiceTest() {
    }

    @BeforeEach
    public void setUp() {
        commentService = new CommentService(commentsRepository, new CommentsMapperImpl(),adsRepository,usersRepository);
        Role role = new Role();
        role.setId(1);
        role.setName("USER");

        testUser = UserEntity.builder()
                .id(1)
                .firstName("Mad")
                .lastName("Hatter")
                .phone("123123123")
                .username("testUser")
                .password("123")
                .roles(new ArrayList<>(List.of(role)))
                .build();

        adEntityTest = AdEntity.builder()
                .pk(12)
                .price(300)
                .title("The Hat")
                .description("old hat")
                .author(testUser)
                .build();

        testCommentEntity = CommentEntity.builder()
                .text(testString)
                .adEntity(adEntityTest)
                .userEntity(testUser)
                .commentId(123)
                .build();
    }


    @Test
    void addNewCommentTest() {
        when(adsRepository.findById(anyInt())).thenReturn(Optional.of(adEntityTest));
        UserDetails testUser1 = User.withUsername("testUser").password("123").roles("USER").build();
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText(testString);
        CommentDto result = commentService.addNewComment(testUser.getId(), createOrUpdateComment,testUser1);
        assertEquals(testUser.getId(), result.getAuthor());
        assertEquals(testString, result.getText());
    }

    @Test
    void deleteComment() {
        UserDetails testUser1 = User.withUsername("testUser").password("123").roles("USER").build();
        when(commentsRepository.findById(anyInt())).thenReturn(Optional.of(testCommentEntity));
        commentService.deleteComment(adEntityTest.getPk(), testCommentEntity.getCommentId(), testUser1);
    }
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