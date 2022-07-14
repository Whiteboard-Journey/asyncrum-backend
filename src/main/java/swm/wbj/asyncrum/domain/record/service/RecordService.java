package swm.wbj.asyncrum.domain.record.service;

import swm.wbj.asyncrum.domain.record.dto.*;

public interface RecordService {
    RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto);
    RecordReadResponseDto readRecord(Long id);
    RecordReadAllResponseDto readAllRecord(Integer pageIndex, Long topId);
    RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto);
    void deleteRecord(Long id);
}
