package swm.wbj.asyncrum.domain.record.bookmark.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.entity.VideoBookmarkCoordinates;

@Data
public class BookmarkReadResponseDto {

    private Long id;
    private Long recordId;
    private String emoji;
    private String content;
    private Double time;
    private VideoBookmarkCoordinates position;
    private String drawing;
    private Double scale;
    private String author;

    public BookmarkReadResponseDto(Bookmark bookmark) {
        this.id = bookmark.getId();
        this.recordId = bookmark.getRecord().getId();
        this.emoji = bookmark.getEmoji();
        this.content = bookmark.getContent();
        this.time = bookmark.getTime();
        this.position = bookmark.getPosition();
        this.drawing = bookmark.getDrawing();
        this.scale = bookmark.getScale();
        this.author = bookmark.getMember().getFullname();
    }
}
