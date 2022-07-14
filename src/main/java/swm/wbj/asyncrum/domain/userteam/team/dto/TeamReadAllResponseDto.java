package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamReadAllResponseDto {

    private List<Team> teamList;
    private Pageable pageable;
    private Boolean isList;

}
