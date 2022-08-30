package swm.wbj.asyncrum.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 유저 Role Enum
 */
@Getter
@AllArgsConstructor
public enum RoleType {

    // TODO: 이후에 팀 관리자, 스크럼 마스터, PO 등 세분화된 Role 추가
    ADMIN("ROLE_ADMIN", "관리자 권한"),
    USER("ROLE_USER", "일반 사용자 권한"),
    GUEST("GUEST", "게스트 권한");

    private final String code;
    private final String name;

    public static RoleType of(String code) {
        if (code == null) {
            throw new IllegalArgumentException("권한 설정이 되어 있지 않습니다.");
        }

        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
