package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberDto {

    private Long id;

    private String name;

    private String email;

    private String pictureUrl;

    private String phone;

    private String nickname;
}
