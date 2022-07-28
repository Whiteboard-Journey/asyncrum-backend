package swm.wbj.asyncrum.domain.userteam.team.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.team.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamService;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateRequestDto requestDto) {
        try {
            TeamCreateResponseDto responseDto = teamService.createTeam(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readTeam(@PathVariable Long id) {
        try {
            TeamReadResponseDto responseDto = teamService.readTeam(id);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> readAllTeam(
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "topId", required = false, defaultValue = "0") Long topId)
    {
        try {
            TeamReadAllResponseDto responseDto = teamService.readAllTeam(pageIndex, topId);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * 초대 링크를 통한 팀원 초대 (팀원이 이미 가입한 경우만)
     * TODO: 팀원이 아직 서비스에 가입하지 않은 경우 고려
     */
    @PostMapping("/{id}/members/invitation")
    public ResponseEntity<?> sendTeamInvitationLinkByEmail(@PathVariable Long id, @RequestBody TeamMemberAddRequestDto requestDto) {
        try {
            teamService.sendTeamInvitationLinkByEmail(id, requestDto);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * 초대 링크 검증 후 팀원 추가
     * TODO: 프론트엔드 특정 페이지로 REDIRECT (JWT 토큰 없이 가능)
     */
    @GetMapping("/{id}/members/invitation")
    public String verifyTeamInvitationLinkAndAddMember(@PathVariable Long id, @RequestParam("memberId") Long memberId) {
        try {
            teamService.verifyTeamInvitationLinkAndAddMember(id, memberId);
            return "팀 합류 성공";
        } catch (Exception e){
            return "팀 합류 실패";
        }
    }

    /**
     * 수동으로 팀원 추가
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestBody TeamMemberAddRequestDto requestDto) {
        try {
            TeamMemberAddResponseDto responseDto = teamService.addMember(id, requestDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    /**
     * 팀원 제외
     */
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        try {
            teamService.removeMember(id, memberId);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @RequestBody TeamUpdateRequestDto requestDto) {
        try {
            TeamUpdateResponseDto responseDto = teamService.updateTeam(id, requestDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
        }
    }

}
