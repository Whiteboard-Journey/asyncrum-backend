package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberReadResponseDto;
import swm.wbj.asyncrum.global.type.ScopeType;

@Data
public class RecordReadResponseDto {

    private String title;
    private String description;
    private String recordUrl;
    private String projectMetadata;
    private ScopeType scope;

    private MemberReadResponseDto member;

    public RecordReadResponseDto(Record record){
        this.title = record.getTitle();
        this.description = record.getDescription();
        this.recordUrl = record.getRecordFileUrl();
        this.scope = record.getScope();
        this.projectMetadata = record.getProjectMetadata();
        this.member = new MemberReadResponseDto(record.getMember());
    }
}
