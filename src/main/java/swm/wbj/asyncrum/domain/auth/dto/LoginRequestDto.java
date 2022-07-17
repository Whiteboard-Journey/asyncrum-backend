package swm.wbj.asyncrum.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    private String email;
    private String password;

}
