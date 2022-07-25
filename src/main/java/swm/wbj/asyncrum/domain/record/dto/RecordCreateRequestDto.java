package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.entity.Record;

@Data
@Getter
@NoArgsConstructor
public class RecordCreateRequestDto {

    private String videoUrl;
    private String title;
    private String description;
    private String scope;

    public Record toEntity(){
        return Record.builder()
                .videoUrl(videoUrl)
                .title(title)
                .description(description)
                .scope(scope)
                .build();
    }
}
