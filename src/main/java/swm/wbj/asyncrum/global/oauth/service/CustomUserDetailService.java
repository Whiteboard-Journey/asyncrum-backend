package swm.wbj.asyncrum.global.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.oauth.entity.UserPrincipal;

/**
 * 사용자 인증 정보를 가져오는 서비스
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(MemberNotExistsException::new);

        if(member == null) {
            throw new UsernameNotFoundException("해당 Email 을 가진 멤버가 없습니다.");
        }

        return UserPrincipal.create(member);
    }
}
