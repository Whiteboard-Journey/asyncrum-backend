package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberReadAllResponseDto {

    private List<Member> memberList;
    private Pageable pageable;
    private Boolean isList;
}
