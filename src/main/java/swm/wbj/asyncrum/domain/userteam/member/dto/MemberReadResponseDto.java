package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Data
public class MemberReadResponseDto {
    private String name;
    private String email;
    private String pictureUrl;
    private String phone;
    private String nickname;

    public MemberReadResponseDto(Member member){
        this.name = member.getName();
        this.email = member.getEmail();
        this.pictureUrl = member.getPictureUrl();
        this.phone = member.getPhone();
        this.nickname = member.getNickname();
    }
}
