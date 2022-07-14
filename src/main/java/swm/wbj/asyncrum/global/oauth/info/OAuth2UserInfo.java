package swm.wbj.asyncrum.global.oauth.info;

import java.util.Map;

/**
 *  OAuth2 로그인으로 얻은 사용자 정보를 나타낸 abstract class
 */
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
