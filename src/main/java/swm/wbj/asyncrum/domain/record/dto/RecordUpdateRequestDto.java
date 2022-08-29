package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RecordUpdateRequestDto {
    private String title;
    private String description;
    private String scope;
}
