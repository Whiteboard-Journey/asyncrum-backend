package swm.wbj.asyncrum.domain.whiteboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardService;
import swm.wbj.asyncrum.global.annotation.AdminRole;
import swm.wbj.asyncrum.global.exception.ErrorResponseDto;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/whiteboards")
@RestController
public class WhiteboardController {

    private final WhiteboardService whiteboardService;

    @PostMapping
    public ResponseEntity<?> createWhiteboard(@Valid @RequestBody WhiteboardCreateRequestDto requestDto) {
        try {
            WhiteboardCreateResponseDto responseDto = whiteboardService.createWhiteboard(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

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

    @GetMapping(params = "scope")
    public ResponseEntity<?> readAllWhiteboard(
            @RequestParam(value = "scope") ScopeType scope,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        try {
            WhiteboardReadAllResponseDto responseDto = whiteboardService.readAllWhiteboard(scope, pageIndex, topId, sizePerPage);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @AdminRole
    @GetMapping(params = "!scope")
    public ResponseEntity<?> readAllWhiteboard(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        try {
            WhiteboardReadAllResponseDto responseDto = whiteboardService.readAllWhiteboard(pageIndex, topId, sizePerPage);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateWhiteboard(@PathVariable Long id, @Valid @RequestBody WhiteboardUpdateRequestDto requestDto) {
        try {
            WhiteboardUpdateResponseDto responseDto = whiteboardService.updateWhiteboard(id, requestDto);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
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
