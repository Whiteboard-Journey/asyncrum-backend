package swm.wbj.asyncrum.domain.record.bookmark.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookmarkReadAllResponseDto {

    private List<BookmarkReadResponseDto> bookmarks;

    public BookmarkReadAllResponseDto(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks.stream()
                .map(BookmarkReadResponseDto::new)
                .collect(Collectors.toList());
    }
}
