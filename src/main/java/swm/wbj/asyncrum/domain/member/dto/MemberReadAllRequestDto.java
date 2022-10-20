package swm.wbj.asyncrum.domain.member.dto;

import lombok.Data;

@Data
public class MemberReadAllRequestDto {

    private Integer pageIndex;
    private Long topId;
}
