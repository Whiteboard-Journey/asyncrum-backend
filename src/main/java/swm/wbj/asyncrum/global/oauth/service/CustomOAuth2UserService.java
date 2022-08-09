package swm.wbj.asyncrum.global.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.oauth.entity.UserPrincipal;
import swm.wbj.asyncrum.global.oauth.exception.OAuthProviderMissMatchException;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfo;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfoFactory;

/**
 * Provider로부터 받은 OAuth2 유저(User) 정보를 기존 계정(Memeber)과 연계하여 처리(회원가입, 정보 갱신 등)하는 서비스
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Provider 로부터 받은 OAuth2 User 정보를 Member 엔티티와 연계하여 처리
     */
    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        // OAuth2 유저 정보의 고유 email 정보를 통해 해당 email을 가지고 있는 Member 검색
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Member savedMember = memberRepository.findByEmail(userInfo.getEmail());

        // 해당 Member가 존재한다면
        if (savedMember != null) {
            // 그런데 Provider가 다르다면, Provider mismatch 예외처리
            if (providerType != savedMember.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                        " account. Please use your " + savedMember.getProviderType() + " account to login."
                );
            }

            // Provider가 같다면 기존 Member 정보 갱신
            updateMember(savedMember, userInfo);
        }
        else {
            // 해당 Member가 존재하지 않는다면 새로운 Member 생성 (회원가입)
            savedMember = createMember(userInfo, providerType);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    // 새로운 Member 생성 (회원가입)
    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        Member member = Member.createMember()
                .email(userInfo.getEmail())
                .oauthId(userInfo.getId())
                .fullname(userInfo.getName())
                .profileImageUrl(userInfo.getImageUrl())
                .roleType(RoleType.USER)
                .providerType(providerType)
                .build();

        return memberRepository.saveAndFlush(member);
    }

    // 기존 Member 정보 갱신
    private Member updateMember(Member member, OAuth2UserInfo userInfo) {
        if ((userInfo.getName() != null && !member.getFullname().equals(userInfo.getName())) &&
                (userInfo.getImageUrl() != null && !member.getProfileImageUrl().equals(userInfo.getImageUrl()))) {
            // fullname은 다시 업데이트 x
            member.update(null, userInfo.getImageUrl());
        }

        return member;
    }
}
