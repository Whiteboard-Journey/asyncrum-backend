package swm.wbj.asyncrum.domain.record.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.bookmark.dto.*;
import swm.wbj.asyncrum.domain.record.bookmark.service.BookmarkService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<?> createBookmark(@RequestBody BookmarkCreateRequestDto requestDto) {
        BookmarkCreateResponseDto responseDto = bookmarkService.createBookmark(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> createBookmark(@PathVariable Long id) {
        BookmarkReadResponseDto responseDto = bookmarkService.readBookmark(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<?> readAllBookmark(@RequestParam("recordId") Long recordId) {
        BookmarkReadAllResponseDto responseDto = bookmarkService.readAllBookmark(recordId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBookmark(
            @PathVariable Long id,
            @RequestBody BookmarkUpdateRequestDto requestDto) {
        BookmarkUpdateResponseDto responseDto = bookmarkService.updateBookmark(id, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);

        return ResponseEntity.noContent().build();
    }
}
