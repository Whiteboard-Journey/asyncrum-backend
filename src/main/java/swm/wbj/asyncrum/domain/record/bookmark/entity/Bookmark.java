package swm.wbj.asyncrum.domain.record.bookmark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.member.entity.Member;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookmark")
@Entity
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(targetEntity = Record.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String emoji;

    @Column
    private String content;

    @Column
    private Double time;

    @Embedded
    @Column
    private VideoBookmarkCoordinates position;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String drawing;

    @Column
    private Double scale;

    @Builder(builderMethodName = "createBookmark")

    public Bookmark(Record record,
                    Member member,
                    String emoji,
                    String content,
                    Double time,
                    VideoBookmarkCoordinates position,
                    String drawing,
                    Double scale) {
        this.record = record;
        this.member = member;
        this.emoji = emoji;
        this.content = content;
        this.time = time;
        this.position = position;
        this.drawing = drawing;
        this.scale = scale;
    }

    public void updateBookmark(String emoji,
                               String content,
                               Double time,
                               VideoBookmarkCoordinates position,
                               String drawing,
                               Double scale) {
        if(emoji != null) this.emoji = emoji;
        if(content != null) this.content = content;
        if(time != null) this.time = time;
        if(position != null) this.position = position;
        if(drawing != null) this.drawing = drawing;
        if(scale != null) this.scale = scale;
    }
}
