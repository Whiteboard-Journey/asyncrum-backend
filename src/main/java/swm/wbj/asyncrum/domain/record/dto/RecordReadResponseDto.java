package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.entity.Record;

@Data
public class RecordReadResponseDto {
    private String videoUrl;
    private String title;
    private String description;
    private String scope;

    public RecordReadResponseDto(Record record){
        this.videoUrl = record.getVideoUrl();
        this.title = record.getTitle();
        this.description = record.getDescription();
        this.scope = record.getScope();
    }
}
