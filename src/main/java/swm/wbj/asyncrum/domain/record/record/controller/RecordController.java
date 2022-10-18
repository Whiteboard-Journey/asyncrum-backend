package swm.wbj.asyncrum.domain.record.record.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.record.dto.*;
import swm.wbj.asyncrum.domain.record.record.service.RecordService;
import swm.wbj.asyncrum.global.type.ScopeType;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Record")
@RequestMapping("/api/v1/records")
@RestController
public class RecordController {

    private final RecordService recordService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRecord(@RequestBody RecordCreateRequestDto requestDto) {
        RecordCreateResponseDto responseDto= recordService.createRecord(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?>  readRecord(@PathVariable("id") Long id) {
        RecordReadResponseDto responseDto = recordService.readRecord(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllRecord(
            @RequestParam(value = "teamId") Long teamId,
            @RequestParam(value = "scope") ScopeType scope,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer sizePerPage)
    {
        RecordReadAllResponseDto responseDto =
                recordService.readAllRecord(teamId, scope, pageIndex, topId, sizePerPage);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRecord(
            @PathVariable Long id,
            @RequestBody RecordUpdateRequestDto requestDto)
    {
        RecordUpdateResponseDto responseDto = recordService.updateRecord(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteRecord(@PathVariable("id") Long id) {
        recordService.deleteRecord(id);

        return ResponseEntity.noContent().build();
    }
}
