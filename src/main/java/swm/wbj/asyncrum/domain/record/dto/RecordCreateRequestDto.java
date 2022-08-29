package swm.wbj.asyncrum.domain.record.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.HashSet;
import java.util.Set;


@Data
@Getter
@NoArgsConstructor
public class RecordCreateRequestDto {

    private String title;
    private String description;
    private String scope;
    private Set<Long> seenMemberIdGroup;


    public Record toEntity(Member author){
        return Record.createRecord()
                .title(title)
                .description(description)
                .scope(ScopeType.of(scope))
                .author(author)
                .seenMemberIdGroup(seenMemberIdGroup)
                .build();
    }
}
