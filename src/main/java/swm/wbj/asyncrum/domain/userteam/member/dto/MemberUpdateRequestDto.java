package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Data;

@Data
public class MemberUpdateRequestDto {

    private String username;
    private String pictureUrl;
    private String phone;
    private String nickname;

}
