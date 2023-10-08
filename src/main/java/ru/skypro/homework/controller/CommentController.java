package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/")
public class CommentController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}/comments")
    public CommentsDto getComments(@PathVariable long id) {
        ArrayList<CommentDto> listComments = new ArrayList<>(List.of(new CommentDto(), new CommentDto()));
        CommentsDto commentsDto = new CommentsDto();
        commentsDto.setResults(listComments);
        return commentsDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "{id}/comments")
    public CommentDto addComment(@PathVariable long id, @RequestBody String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable long adId, @PathVariable long commentId) {

    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("{adId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable long adId,
                                                     @PathVariable long commentId,
                                                     @RequestBody String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        return commentDto;
    }
}
