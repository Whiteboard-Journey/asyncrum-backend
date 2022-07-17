package swm.wbj.asyncrum.global.oauth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 현재 인증된 사용자 정보를 보관하는 객체
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser {

    private final String oauthId;
    private final String email;
    private final String password;
    private final ProviderType providerType;
    private final RoleType roleType;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return oauthId;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    public static UserPrincipal create(Member member) {
        return new UserPrincipal(
                member.getOauthId(),
                member.getEmail(),
                member.getPassword(),
                member.getProviderType(),
                RoleType.USER,
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()))
        );
    }

    public static UserPrincipal create(Member member, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(member);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }
}
