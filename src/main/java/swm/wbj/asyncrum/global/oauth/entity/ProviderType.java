package swm.wbj.asyncrum.global.oauth.entity;

import lombok.Getter;

/**
 * Provider (OAuth2 제공 서비스) Enum
 */
@Getter
public enum ProviderType {
    GOOGLE,
    FACEBOOK,
    NAVER,
    KAKAO,
    LOCAL;
}
