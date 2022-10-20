package swm.wbj.asyncrum.domain.record.record.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.bookmark.dto.BookmarkReadResponseDto;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.member.dto.MemberReadResponseDto;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecordReadResponseDto {

    private Long id;
    private String title;
    private String description;
    private String recordUrl;
    private List<BookmarkReadResponseDto> bookmarks;
    private ScopeType scope;
    private MemberReadResponseDto member;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public RecordReadResponseDto(Record record){
        this.id = record.getId();
        this.title = record.getTitle();
        this.description = record.getDescription();
        this.recordUrl = record.getRecordFileUrl();
        this.scope = record.getScope();
        this.bookmarks = record.getBookmarks().stream()
                .map(BookmarkReadResponseDto::new)
                .collect(Collectors.toList());
        this.member = new MemberReadResponseDto(record.getMember());
        this.createdDate = record.getCreatedDate();
        this.lastModifiedDate = record.getLastModifiedDate();
    }
}
