package swm.wbj.asyncrum.domain.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.utils.UiAvatarService;

@Getter
@NoArgsConstructor
@Data
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
                .profileImageUrl(UiAvatarService.getProfileImageUrl(fullname))
                .build();
    }
}
