package swm.wbj.asyncrum.domain.record.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.dto.*;
import swm.wbj.asyncrum.domain.record.service.RecordService;
import swm.wbj.asyncrum.global.exception.ErrorResponseDto;
import swm.wbj.asyncrum.global.type.ScopeType;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
@RestController
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody RecordCreateRequestDto requestDto){
        RecordCreateResponseDto responseDto= recordService.createRecord(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>  readRecord(@PathVariable("id") Long id){
        RecordReadResponseDto responseDto = recordService.readRecord(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<?> readAllRecord(
            @RequestParam(value = "scope") ScopeType scope,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        RecordReadAllResponseDto responseDto = recordService.readAllRecord(scope, pageIndex, topId, sizePerPage);

        return ResponseEntity.ok(responseDto);
    }



    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateRequestDto requestDto){
        RecordUpdateResponseDto responseDto = recordService.updateRecord(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable("id") Long id){
        recordService.deleteRecord(id);

        return ResponseEntity.noContent().build();
    }
}
