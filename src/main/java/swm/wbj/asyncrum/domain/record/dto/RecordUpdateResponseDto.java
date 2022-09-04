package swm.wbj.asyncrum.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecordUpdateResponseDto {

    private Long id;
    private String preSignedURL;
}
