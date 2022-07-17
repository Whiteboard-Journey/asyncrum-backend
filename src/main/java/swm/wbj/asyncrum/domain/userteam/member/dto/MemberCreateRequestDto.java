package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

@Data
@Getter
@NoArgsConstructor
public class MemberCreateRequestDto {

    private String email;
    private String password;
    private String nickname;

    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.createMember()
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
    }

}
