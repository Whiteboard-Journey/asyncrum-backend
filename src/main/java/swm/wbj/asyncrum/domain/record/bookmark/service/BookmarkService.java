package swm.wbj.asyncrum.domain.record.bookmark.service;

import swm.wbj.asyncrum.domain.record.bookmark.dto.*;

public interface BookmarkService {

    BookmarkCreateResponseDto createBookmark(BookmarkCreateRequestDto requestDto);
    BookmarkReadResponseDto readBookmark(Long id);
    BookmarkReadAllResponseDto readAllBookmark(Long recordId);
    BookmarkUpdateResponseDto updateBookmark(Long id, BookmarkUpdateRequestDto requestDto);
    void deleteBookmark(Long id);
}
