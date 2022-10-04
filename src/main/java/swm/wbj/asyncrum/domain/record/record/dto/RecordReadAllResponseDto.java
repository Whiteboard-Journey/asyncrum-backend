package swm.wbj.asyncrum.domain.record.record.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.record.record.entity.Record;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecordReadAllResponseDto {

    private List<RecordReadResponseDto> records;
    private Pageable pageable;
    private Boolean isList;

    public RecordReadAllResponseDto(List<Record> records, Pageable pageable, Boolean isList) {
        this.records = records.stream()
                .map(RecordReadResponseDto::new)
                .collect(Collectors.toList());
        this.pageable = pageable;
        this.isList = isList;
    }
}
