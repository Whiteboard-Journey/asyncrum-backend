package swm.wbj.asyncrum.domain.record.bookmark.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.bookmark.entity.VideoBookmarkCoordinates;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Data
@Getter
@NoArgsConstructor
public class BookmarkCreateRequestDto {

    private Long recordId;
    private String content;
    private Double time;
    private VideoBookmarkCoordinates position;
    private String drawing;
    private Double scale;

    public Bookmark toEntity(Record record, Member member) {
        return Bookmark.createBookmark()
                .record(record)
                .member(member)
                .content(content)
                .time(time)
                .position(position)
                .drawing(drawing)
                .scale(scale)
                .build();
    }
}
