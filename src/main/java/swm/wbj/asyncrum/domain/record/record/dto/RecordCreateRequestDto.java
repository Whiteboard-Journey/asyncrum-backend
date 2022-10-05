package swm.wbj.asyncrum.domain.record.record.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.type.ScopeType;

@Data
@Getter
@NoArgsConstructor
public class RecordCreateRequestDto {

    private String title;
    private String description;
    private String scope;
    private Long teamId;

    public Record toEntity(Member member, Team team){
        return Record.createRecord()
                .title(title)
                .description(description)
                .scope(ScopeType.of(scope))
                .member(member)
                .team(team)
                .build();
    }
}
