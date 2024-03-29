package swm.wbj.asyncrum.domain.record.record.service;

import swm.wbj.asyncrum.domain.record.record.dto.*;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.global.type.ScopeType;

public interface RecordService {

    RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto);
    Record getCurrentRecord(Long id);
    RecordReadResponseDto readRecord(Long id);
    RecordReadAllResponseDto readAllRecord(Long teamId, ScopeType scope, Integer pageIndex,
                                           Long topId, Integer sizePerPage);
    RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto);
    void deleteRecord(Long id);
}
