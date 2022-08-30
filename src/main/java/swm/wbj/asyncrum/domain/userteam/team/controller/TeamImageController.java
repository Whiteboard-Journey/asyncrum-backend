package swm.wbj.asyncrum.domain.userteam.team.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamImageCreateResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.service.TeamService;
import swm.wbj.asyncrum.global.exception.ErrorResponseDto;

@RestController
@RequestMapping("/api/v1/team/images")
@Log4j2
@RequiredArgsConstructor
public class TeamImageController {

    private final TeamService teamService;

    @PostMapping("/{id}")
    public ResponseEntity<?> createTeamImage(@PathVariable("id") Long id) {
        try {
            TeamImageCreateResponseDto responseDto = teamService.createImage(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto((e.getMessage())));
        }
    }
}
