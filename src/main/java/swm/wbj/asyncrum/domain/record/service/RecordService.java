package swm.wbj.asyncrum.domain.record.service;

import swm.wbj.asyncrum.domain.record.dto.*;

import java.io.IOException;

public interface RecordService {
    RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto) throws IOException;
    RecordReadResponseDto readRecord(Long id) throws Exception;
    RecordReadAllResponseDto readAllRecord(Integer pageIndex, Long topId);
    RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto) throws IOException;
    void deleteRecord(Long id);
    RecordReadDailyResponseDto readDailyRecord(Long topId);

}
