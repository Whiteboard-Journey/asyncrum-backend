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
 * Provider 로부터 받은 OAuth2 유저(User) 정보를 기존 계정(Member)과 연계하여 처리(회원가입, 정보 갱신 등)하는 서비스
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.processMemberByOAuthUser(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processMemberByOAuthUser(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Member savedMember = memberRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (savedMember != null) {
            checkAlreadySignedUpByAnotherProvider(providerType, savedMember);

            updateMember(savedMember, userInfo);
        }
        else {
            savedMember = createMember(userInfo, providerType);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    private void checkAlreadySignedUpByAnotherProvider(ProviderType providerType, Member savedMember) {
        if (providerType != savedMember.getProviderType()) {
            throw new OAuthProviderMissMatchException(
                    "이미 " + providerType + " 계정으로 가입되어 있습니다." +
                    savedMember.getProviderType() + " 계정으로 로그인 해주세요."
            );
        }
    }

    private Member createMember(OAuth2UserInfo userInfo, ProviderType providerType) {
        Member member = Member.createMember()
                .email(userInfo.getEmail())
                .oauthId(userInfo.getId())
                .fullname(userInfo.getName())
                .roleType(RoleType.USER)
                .providerType(providerType)
                .build();

        return memberRepository.saveAndFlush(member);
    }

    private void updateMember(Member member, OAuth2UserInfo userInfo) {
        if (userInfo.getImageUrl() != null && !userInfo.getImageUrl().equals(member.getFullname())) {
            member.updateProfileImage(null, userInfo.getImageUrl());
        }
    }
}
