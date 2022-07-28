package swm.wbj.asyncrum.domain.whiteboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardService;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/whiteboards")
@RestController
public class WhiteboardController {

    private final WhiteboardService whiteboardService;

    // 화이트보드 문서 생성
    @PostMapping
    public ResponseEntity<?> createWhiteboard(@RequestBody WhiteboardCreateRequestDto requestDto) {
        try {
            WhiteboardCreateResponseDto responseDto = whiteboardService.createWhiteboard(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    // 화이트보드 문서 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> readWhiteboard(@PathVariable Long id) {
        try {
            WhiteboardReadResponseDto responseDto = whiteboardService.readWhiteboard(id);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    // 화이트보드 문서 전체 조희
    @GetMapping
    public ResponseEntity<?> readAllWhiteboard(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId)
    {
        try {
            WhiteboardReadAllResponseDto responseDto = whiteboardService.readAllWhiteboard(pageIndex, topId);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    // 화이트보드 문서 정보 업데이트
    @PatchMapping("{id}")
    public ResponseEntity<?> updateWhiteboard(@PathVariable Long id, @RequestBody WhiteboardUpdateRequestDto requestDto) {
        try {
            WhiteboardUpdateResponseDto responseDto = whiteboardService.updateWhiteboard(id, requestDto);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    // 화이트보드 문서 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteWhiteboard(@PathVariable Long id) {
        try {
            whiteboardService.deleteWhiteboard(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }
}
