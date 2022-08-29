package swm.wbj.asyncrum.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.record.entity.Record;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class RecordReadAllResponseDto {
    private List<Record> records;
    private Pageable pageable;
    private Boolean isList;

}
