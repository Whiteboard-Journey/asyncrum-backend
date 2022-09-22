package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.type.RoleType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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
                .profileImageUrl(getProfileImageUrl(fullname))
                .build();
    }

    private static String getProfileImageUrl(String fullname) {
        return "https://ui-avatars.com/api/?name=" + tokenize(fullname);
    }

    /**
     *  1. space를 + 로 치환
     *  2. 대문자 앞에 + 추가. 단, 맨 앞글자는 제외
     */
    private static String tokenize(String fullname) {
        StringBuilder result = new StringBuilder("" + fullname.charAt(0));
        for (int i=1; i< fullname.length(); i++) {
            char c = fullname.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                result.append("+");
                result.append(c);
            }
            else if (c == ' ') {
                result.append("+");
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
