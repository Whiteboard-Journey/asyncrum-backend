package swm.wbj.asyncrum.domain.record.bookmark.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.bookmark.dto.*;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkService;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Bookmark")
@RequestMapping("/api/v1/bookmarks")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBookmark(@RequestBody BookmarkCreateRequestDto requestDto) {
        BookmarkCreateResponseDto responseDto = bookmarkService.createBookmark(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readBookmark(@PathVariable Long id) {
        BookmarkReadResponseDto responseDto = bookmarkService.readBookmark(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllBookmark(@RequestParam("recordId") Long recordId) {
        BookmarkReadAllResponseDto responseDto = bookmarkService.readAllBookmark(recordId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBookmark(
            @PathVariable Long id,
            @RequestBody BookmarkUpdateRequestDto requestDto) {
        BookmarkUpdateResponseDto responseDto = bookmarkService.updateBookmark(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);

        return ResponseEntity.noContent().build();
    }
}
