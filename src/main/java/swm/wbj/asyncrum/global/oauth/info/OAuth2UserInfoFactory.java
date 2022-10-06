package swm.wbj.asyncrum.global.oauth.info;

import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.oauth.info.impl.GoogleOAuth2UserInfo;
import swm.wbj.asyncrum.global.oauth.info.impl.NaverOAuth2UserInfo;

import java.util.Map;

/**
 * OAuth2 사용자 정보 Provider 별로 implementation 하는 Factory
 */
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Unsupported Provider Type");
        }
    }
}
