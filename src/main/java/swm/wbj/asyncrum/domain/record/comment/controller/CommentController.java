package swm.wbj.asyncrum.domain.record.comment.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.comment.dto.*;
import swm.wbj.asyncrum.domain.record.comment.service.CommentService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Comment")
@RequestMapping("/api/v1/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createComment(@RequestBody CommentCreateRequestDto requestDto) {
        CommentCreateResponseDto responseDto = commentService.createComment(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readComment(@PathVariable Long id) {
        CommentReadResponseDto responseDto = commentService.readComment(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllComment(@RequestParam("bookmarkId") Long bookmarkId) {
        CommentReadAllResponseDto responseDto = commentService.readAllComment(bookmarkId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateRequestDto requestDto) {
        CommentUpdateResponseDto responseDto = commentService.updateComment(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);

        return ResponseEntity.noContent().build();
    }
}
