package swm.wbj.asyncrum.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RecordUpdateResponseDto {
    private Long id;
    private String preSignedURL;
}
