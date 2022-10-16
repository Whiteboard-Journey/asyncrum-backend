package swm.wbj.asyncrum.domain.whiteboard.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.whiteboard.dto.*;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardService;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Whiteboard")
@RequestMapping("/api/v1/whiteboards")
@RestController
public class WhiteboardController {

    private final WhiteboardService whiteboardService;

    @PostMapping
    public ResponseEntity<?> createWhiteboard(
            @Valid @RequestBody WhiteboardCreateRequestDto requestDto) {
        WhiteboardCreateResponseDto responseDto = whiteboardService.createWhiteboard(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readWhiteboard(@PathVariable Long id) {
        WhiteboardReadResponseDto responseDto = whiteboardService.readWhiteboard(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<?> readAllWhiteboard(
            @RequestParam(value = "teamId") Long teamId,
            @RequestParam(value = "scope") ScopeType scope,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        WhiteboardReadAllResponseDto responseDto =
                whiteboardService.readAllWhiteboard(teamId, scope, pageIndex, topId, sizePerPage);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateWhiteboard(
            @PathVariable Long id,
            @Valid @RequestBody WhiteboardUpdateRequestDto requestDto) {
        WhiteboardUpdateResponseDto responseDto = whiteboardService.updateWhiteboard(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWhiteboard(@PathVariable Long id) {
        whiteboardService.deleteWhiteboard(id);

        return ResponseEntity.noContent().build();
    }
}
