package swm.wbj.asyncrum.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.record.entity.Record;

import java.util.List;

@Data
@AllArgsConstructor
public class RecordReadDailyResponseDto {
    private List<Record> records;
}
