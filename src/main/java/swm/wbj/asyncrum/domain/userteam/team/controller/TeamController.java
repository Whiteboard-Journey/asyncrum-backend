package swm.wbj.asyncrum.domain.userteam.team.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.team.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamService;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "Team")
@RequestMapping("/api/v1/teams")
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateRequestDto requestDto) {
        TeamCreateResponseDto responseDto = teamService.createTeam(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readTeam(@PathVariable Long id) {
        TeamReadResponseDto responseDto = teamService.readTeam(id);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readAllTeam(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId,
            @RequestParam(value = "sizePerPage", required = false, defaultValue = "12") Integer SIZE_PER_PAGE)
    {
        TeamReadAllResponseDto responseDto = teamService.readAllTeam(pageIndex, topId, SIZE_PER_PAGE);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 초대 링크를 통한 팀원 초대 (팀원이 이미 가입한 경우만)
     * TODO: 팀원이 아직 서비스에 가입하지 않은 경우 고려
     */
    @PostMapping(value = "/{id}/members/invitation", consumes = APPLICATION_JSON_VALUE, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> sendTeamInvitationLinkByEmail(
            @PathVariable Long id,
            @RequestBody TeamMemberAddRequestDto requestDto) throws Exception {
        teamService.sendTeamInvitationLinkByEmail(id, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 초대 링크 검증 후 팀원 추가
     * TODO: 프론트엔드 특정 페이지로 REDIRECT (JWT 토큰 없이 가능)
     */
    @GetMapping(value = "/{id}/members/invitation", produces = TEXT_PLAIN_VALUE)
    public String verifyTeamInvitationLinkAndAddMember(
            @PathVariable Long id,
            @RequestParam("memberId") Long memberId) {
        teamService.verifyTeamInvitationLinkAndAddMember(id, memberId);

        return "팀 합류 성공";
    }

    /**
     * 수동으로 팀원 추가
     */
    @PostMapping(value = "/{id}/members", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestBody TeamMemberAddRequestDto requestDto) {
        TeamMemberAddResponseDto responseDto = teamService.addMember(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(value = "/{id}/roomNames", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRoomName(@PathVariable Long id, @RequestBody TeamMeetingRequestDto requestDto){
        TeamUpdateResponseDto responseDto = teamService.addRoomName(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}/roomNames", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRoomName(@PathVariable Long id, @RequestBody TeamMeetingRequestDto requestDto){
        teamService.removeRoomName(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 팀원 제외
     */
    @DeleteMapping(value = "/{id}/members/{memberId}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        teamService.removeMember(id, memberId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @RequestBody TeamUpdateRequestDto requestDto) {
        TeamUpdateResponseDto responseDto = teamService.updateTeam(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTeamImage(@PathVariable("id") Long id) {
        TeamImageCreateResponseDto responseDto = teamService.createImage(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
