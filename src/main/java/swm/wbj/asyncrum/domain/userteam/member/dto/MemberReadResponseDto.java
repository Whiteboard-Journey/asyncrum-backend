package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Data
public class MemberReadResponseDto {
    private String username;
    private String email;
    private String pictureUrl;
    private String phone;
    private String nickname;

    public MemberReadResponseDto(Member member){
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.pictureUrl = member.getPictureUrl();
        this.phone = member.getPhone();
        this.nickname = member.getNickname();
    }
}
