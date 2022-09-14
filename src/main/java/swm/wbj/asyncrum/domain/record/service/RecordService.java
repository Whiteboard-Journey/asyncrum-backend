package swm.wbj.asyncrum.domain.record.service;

import swm.wbj.asyncrum.domain.record.dto.*;
import swm.wbj.asyncrum.global.type.ScopeType;

public interface RecordService {

    RecordCreateResponseDto createRecord(RecordCreateRequestDto requestDto);
    RecordReadResponseDto readRecord(Long id);
    RecordReadAllResponseDto readAllRecord(Long teamId, ScopeType scope, Integer pageIndex, Long topId, Integer sizePerPage);
    RecordUpdateResponseDto updateRecord(Long id, RecordUpdateRequestDto requestDto);
    void deleteRecord(Long id);
}
