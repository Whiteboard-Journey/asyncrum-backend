package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;

@Data
public class RecordReadDailyRequestDto {
    private Long currentId;
    private Long pageIndex;
    private Long topId;
}
