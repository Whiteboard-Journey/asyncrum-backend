package swm.wbj.asyncrum.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.member.entity.Member;

import java.util.List;

@AllArgsConstructor
@Data
public class MemberReadAllResponseDto {

    private List<Member> memberList;
    private Pageable pageable;
    private Boolean isList;
}
