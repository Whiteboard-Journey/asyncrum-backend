package swm.wbj.asyncrum.domain.whiteboard.service;

import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

public interface WhiteboardService {

    WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto);
    WhiteboardReadResponseDto readWhiteboard(Long id);
    WhiteboardReadAllResponseDto readAllWhiteboard(Integer pageIndex, Long topId, Integer sizePerPage);
    WhiteboardReadAllResponseDto readAllWhiteboard(ScopeType scope, Integer pageIndex, Long topId, Integer sizePerPage);
    WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto);
    void deleteWhiteboard(Long id);
}
