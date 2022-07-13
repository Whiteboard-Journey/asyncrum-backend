package swm.wbj.asyncrum.domain.userteam.team.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.userteam.team.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamServiceImpl;

@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
@RestController
public class TeamController {
    private final TeamServiceImpl teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateRequestDto requestDto) {
        try {
            Long createdId = teamService.createTeam(requestDto);

            return ResponseEntity.ok(createdId);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readTeam(@PathVariable Long id) {
        try {
            TeamReadResponseDto responseDto = teamService.readTeam(id);

            return ResponseEntity.ok(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @RequestBody TeamUpdateRequestDto requestDto) {
        try {
            Long updateId = teamService.updateTeam(id, requestDto);

            return ResponseEntity.ok(updateId);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);

            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
