package swm.wbj.asyncrum.domain.member.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.global.type.RoleType;

import java.util.TimeZone;

@Data
public class MemberReadResponseDto {

    private Long id;
    private String fullname;
    private String profileImageUrl;
    private String email;
    private RoleType roleType;

    private TimeZone timeZone;

    public MemberReadResponseDto(Member member){
        this.id = member.getId();
        this.fullname = member.getFullname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.roleType = member.getRoleType();
        this.timeZone = member.getTimezone();
    }
}
