package swm.wbj.asyncrum.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


/**
 * 유저 Role Enum
 */
@Getter
@AllArgsConstructor
public enum ScopeType {
    //ALL("all", "전체 범위"),
    TEAM("team", "팀 범위"),
    PRIVATE("private", "개인 범위");

    private final String scope;
    private final String name;

    public static ScopeType of(String scope) {
        if (scope == null) {
            throw new IllegalArgumentException("범위 설정이 되어 있지 않습니다.");
        }

        return Arrays.stream(ScopeType.values())
                .filter(r -> r.getScope().equals(scope))
                .findAny()
                .orElse(PRIVATE);
    }
}
