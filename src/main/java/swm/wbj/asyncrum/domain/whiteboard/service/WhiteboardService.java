package swm.wbj.asyncrum.domain.whiteboard.service;

import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

public interface WhiteboardService {

    WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto) throws IOException;

    WhiteboardReadResponseDto readWhiteboard(Long id) throws IOException;

    WhiteboardReadAllResponseDto readAllWhiteboard(ScopeType scope, Integer pageIndex, Long topId);

    WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto) throws IOException;

    void deleteWhiteboard(Long id);
}
