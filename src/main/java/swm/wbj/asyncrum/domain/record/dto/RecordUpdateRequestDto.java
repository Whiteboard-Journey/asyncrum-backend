package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;

@Data
public class RecordUpdateRequestDto {
    private String videoUrl;
    private String title;
    private String description;
    private String scope;
}
