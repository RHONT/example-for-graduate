package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.service.CommentService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/")
public class CommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}/comments")
    public CommentsDto getComments(@PathVariable Integer id) {
        return commentService.getCommentsByAuthorId(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "{id}/comments")
    public CommentDto addComment(@PathVariable Integer id, @RequestBody String text) {

        return commentService.addNewComment(id,text);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable Integer adId, @PathVariable Integer commentId) {
        commentService.deleteComment(adId,commentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("{adId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Integer adId,
                                                     @PathVariable Integer commentId,
                                                     @RequestBody String text) {

        return commentService.updateComment(adId, commentId, text);
    }
}
