package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.validation.constraints.NotNull;

@Data
@Getter
@NoArgsConstructor
public class WhiteboardCreateRequestDto {

    @NotNull
    private String title;
    private String description;
    private String scope;
    private Long teamId;

    public Whiteboard toEntity(Member member, Team team) {
        return Whiteboard.createWhiteboard()
                .title(title)
                .description(description)
                .scope(ScopeType.of(scope))
                .member(member)
                .team(team)
                .build();
    }
}
