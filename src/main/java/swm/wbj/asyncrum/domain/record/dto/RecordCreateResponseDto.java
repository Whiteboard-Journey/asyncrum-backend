package swm.wbj.asyncrum.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.entity.Record;

@Data
@AllArgsConstructor
public class RecordCreateResponseDto {
    private Long id;
    private String preSignedURL;
}
