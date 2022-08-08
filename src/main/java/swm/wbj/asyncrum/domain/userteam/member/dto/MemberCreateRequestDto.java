package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.type.RoleType;

@Data
@Getter
@NoArgsConstructor
public class MemberCreateRequestDto {

    private String email;
    private String password;
    private String fullname;

    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullname(fullname)
                .roleType(RoleType.GUEST)
                .build();
    }

}
