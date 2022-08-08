package swm.wbj.asyncrum.domain.whiteboard.service;

import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.io.IOException;

public interface WhiteboardService {

    // 화이트보드 문서 생성
    WhiteboardCreateResponseDto createWhiteboard(WhiteboardCreateRequestDto requestDto) throws IOException;

    // 화이트보드 문서 개별 조회
    WhiteboardReadResponseDto readWhiteboard(Long id) throws IOException;

    // 화이트보드 문서 전체 조희
    WhiteboardReadAllResponseDto readAllWhiteboard(ScopeType scope, Integer pageIndex, Long topId);

    // 화이트보드 문서 정보 업데이트
    WhiteboardUpdateResponseDto updateWhiteboard(Long id, WhiteboardUpdateRequestDto requestDto) throws IOException;

    // 화이트보드 문서 삭제
    void deleteWhiteboard(Long id);
}
