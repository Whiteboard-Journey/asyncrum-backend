package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;

@Data
public class RecordReadAllRequestDto {
    private Integer pageIndex;
    private Long topId;
}
