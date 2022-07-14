package swm.wbj.asyncrum.global.oauth.info.impl;

import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfo;

import java.util.Map;

/**
 *  OAuth2UserInfo 를 상속한, Google 서비스의 사용자 정보
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
