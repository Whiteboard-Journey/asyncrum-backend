package swm.wbj.asyncrum.domain.meeting.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.meeting.dto.*;
import swm.wbj.asyncrum.domain.meeting.service.MeetingService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Meeting")
@RequestMapping("api/v1/meetings")
@RestController
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMeeting(@RequestBody MeetingCreateRequestDto requestDto) {
        MeetingCreateResponseDto responseDto = meetingService.createMeeting(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readMeeting(@PathVariable Long id) {
        MeetingReadResponseDto responseDto = meetingService.readMeeting(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllMeeting(@RequestParam("teamId") Long teamId) {
        MeetingReadAllResponseDto responseDto = meetingService.readAllMeeting(teamId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMeeting(
            @PathVariable Long id,
            @RequestBody MeetingUpdateRequestDto requestDto) {
        MeetingUpdateResponseDto responseDto = meetingService.updateMeeting(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);

        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/{id}/members", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMeetingMember(@PathVariable Long id, @RequestBody TeamMeetingRequestDto requestDto){
        MeetingUpdateResponseDto responseDto = meetingService.addMeetingMember(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}/members/{memberId}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteMeetingMember(@PathVariable Long id, @PathVariable Long memberId){
        meetingService.removeMeetingMember(id, memberId);
        return ResponseEntity.noContent().build();
    }
}
