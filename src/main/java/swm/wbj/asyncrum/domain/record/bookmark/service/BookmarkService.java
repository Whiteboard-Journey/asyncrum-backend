package swm.wbj.asyncrum.domain.record.bookmark.service;

import swm.wbj.asyncrum.domain.record.bookmark.dto.*;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;

public interface BookmarkService {

    BookmarkCreateResponseDto createBookmark(BookmarkCreateRequestDto requestDto) throws Exception;
    BookmarkReadResponseDto readBookmark(Long id);

    Bookmark getCurrentBookmark(Long id);
    BookmarkReadAllResponseDto readAllBookmark(Long recordId);
    BookmarkUpdateResponseDto updateBookmark(Long id, BookmarkUpdateRequestDto requestDto) throws Exception;
    void deleteBookmark(Long id);
}
