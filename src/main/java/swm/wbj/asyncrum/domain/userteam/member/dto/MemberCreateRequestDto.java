package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Data
@Getter
@NoArgsConstructor
public class MemberCreateRequestDto {

    private String username;
    private String email;
    private String pictureUrl;
    private String phone;
    private String nickname;

    public Member toEntity(){
        return Member.builder()
                .username(username)
                .email(email)
                .pictureUrl(pictureUrl)
                .phone(phone)
                .nickname(nickname)
                .build();
    }
}
