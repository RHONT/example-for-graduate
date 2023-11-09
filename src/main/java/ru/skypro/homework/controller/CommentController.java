package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/")

public class CommentController {
    private final CommentService commentService;


    @Operation(
            summary = "Найти комментарий по ID пользователя",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Комментарий найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Integer.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Комментарий"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{id}/comments")
    public CommentsDto getComments(@PathVariable Integer id) {
        return commentService.getCommentsByIdAd(id);
    }


    @Operation(
            summary = "Добавление комментария",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Комментарий успешно добавлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Комментарий"
    )
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "{id}/comments")
    public CommentDto addComment(@PathVariable Integer id,
                                 @RequestBody CreateOrUpdateComment CreateOrUpdateComment,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.addNewComment(id,CreateOrUpdateComment,userDetails);
    }

    @Operation(
            summary = "Удаление комментария",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Комментарий успешно удален"
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Комментарий"
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping(path = "{adId}/comments/{commentId}")
    public void deleteComment(@PathVariable Integer adId, @PathVariable Integer commentId, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(adId,commentId,userDetails);
    }

    @Operation(
            summary = "Изменение комментария",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Комментарий успешно изменен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))
                    )
            },
            tags = "Комментарий"
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PatchMapping("{adId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Integer adId,
                                                     @PathVariable Integer commentId,
                                                     @RequestBody CreateOrUpdateComment commentUpdate,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        return commentService.updateComment(commentId,userDetails, commentUpdate);
    }
}
