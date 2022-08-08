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
    TEAM("SCOPE_TEAM", "팀 범위"),
    PRIVATE("SCOPE_PRIVATE", "개인 범위");

    private final String scope;
    private final String name;

    public static ScopeType of(String scope) {
        return Arrays.stream(ScopeType.values())
                .filter(r -> r.getScope().equals(scope))
                .findAny()
                .orElse(PRIVATE);
    }
}
