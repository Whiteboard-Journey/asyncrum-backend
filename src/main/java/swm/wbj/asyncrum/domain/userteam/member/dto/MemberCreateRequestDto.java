package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.oauth.entity.RoleType;

@Data
@Getter
@NoArgsConstructor
public class MemberCreateRequestDto {

    private String email;
    private String password;
    private String nickname;

    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .roleType(RoleType.USER)
                .build();
    }

}
