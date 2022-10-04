package swm.wbj.asyncrum.domain.record.record.dto;

import lombok.Data;

@Data
public class RecordUpdateRequestDto {

    private String title;
    private String description;
    private String projectMetadata;
    private String scope;
}
