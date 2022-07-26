package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.entity.RoleType;

@Data
public class MemberReadResponseDto {

    private String fullname;
    private String profileImageUrl;
    private String email;
    private RoleType roleType;

    public MemberReadResponseDto(Member member){
        this.fullname = member.getFullname();
        this.email = member.getEmail();
        this.profileImageUrl = member.getProfileImageUrl();
        this.roleType = member.getRoleType();
    }

}
