package swm.wbj.asyncrum.domain.record.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swm.wbj.asyncrum.domain.record.dto.RecordReadDailyResponseDto;
import swm.wbj.asyncrum.domain.record.service.RecordService;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;

@RestController
@Log4j2
@RequiredArgsConstructor
public class DailyController {
    private final RecordService recordService;

    @GetMapping("/api/v1/daily")
    public ResponseEntity<?> readLeftDailyRecord(
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId
            )
    {
        try{
            RecordReadDailyResponseDto responseDto = recordService.readDailyRecord(topId);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }

    }
}
