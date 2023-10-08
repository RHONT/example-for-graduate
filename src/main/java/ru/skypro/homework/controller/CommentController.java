package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentsDto;

import java.util.List;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    @GetMapping
    public ResponseEntity<List<CommentsDto>> getComments(@PathVariable("id") long adId) {
        return ResponseEntity.ok(List.of(new CommentsDto()));
    }

    @PostMapping
    public ResponseEntity<CommentsDto> addComment(@PathVariable("id") long adId, @RequestBody String text) {
        return ResponseEntity.ok(new CommentsDto());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("adId") long adId, @PathVariable("commentId") long id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentsDto> updateComment(@PathVariable("adId") long adId, @PathVariable("commentId") long Id,
                                                 @RequestBody String text) {
        return ResponseEntity.ok(new CommentsDto());
    }
}
