package swm.wbj.asyncrum.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 팀 내 유저 Role Enum
 */
@Getter
@AllArgsConstructor
public enum TeamRoleType {

    // TODO: 이후에 팀 관리자, 스크럼 마스터, PO 등 세분화된 Role 추가
    OWNER("ROLE_OWNER", "팀 소유자 권한"),
    USER("ROLE_USER", "일반 사용자 권한");

    private final String code;
    private final String name;

    public static TeamRoleType of(String code) {
        if (code == null) {
            throw new IllegalArgumentException("권한 설정이 되어 있지 않습니다.");
        }

        return Arrays.stream(TeamRoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(USER);
    }
}
