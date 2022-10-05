package swm.wbj.asyncrum.domain.record.bookmark.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.bookmark.entity.VideoBookmarkCoordinates;

@Data
public class BookmarkUpdateRequestDto {

    private String content;
    private Double time;
    private VideoBookmarkCoordinates position;
    private String drawing;
    private Double scale;
}
