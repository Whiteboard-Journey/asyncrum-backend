package swm.wbj.asyncrum.domain.member.dto;

import lombok.Data;

@Data
public class MemberUpdateRequestDto {

    private String fullname;
    private String timezone;
    private String fcmRegistrationToken;
}
