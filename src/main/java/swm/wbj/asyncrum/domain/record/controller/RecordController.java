package swm.wbj.asyncrum.domain.record.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.record.dto.*;
import swm.wbj.asyncrum.domain.record.service.RecordService;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.dto.ErrorResponseDto;

@RestController
@Log4j2
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @PostMapping("/api/v1/records")
    public ResponseEntity<?> createRecord(@RequestBody RecordCreateRequestDto requestDto){
        try{
            RecordCreateResponseDto responseDto= recordService.createRecord(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/api/v1/records/{id}")
    public ResponseEntity<?>  readRecord(@PathVariable("id") Long id){
        try{
            RecordReadResponseDto responseDto = recordService.readRecord(id);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/api/v1/records")
    public ResponseEntity<?> readAllRecord(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId)
    {
        try{
            RecordReadAllResponseDto responseDto = recordService.readAllRecord(pageIndex, topId);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/api/v1/records/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateRequestDto requestDto){
        try {
            RecordUpdateResponseDto responseDto = recordService.updateRecord(id, requestDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @DeleteMapping("/api/v1/records/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable("id") Long id){
        try {
            recordService.deleteRecord(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

}
