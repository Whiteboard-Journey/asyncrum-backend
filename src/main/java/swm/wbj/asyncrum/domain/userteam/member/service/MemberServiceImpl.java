package swm.wbj.asyncrum.domain.userteam.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamReadAllResponseDto;
import swm.wbj.asyncrum.global.oauth.utils.TokenUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {
        Member member = requestDto.toEntity(passwordEncoder);
        return new MemberCreateResponseDto(memberRepository.save(member).getId());
    }

    /**
     * 요청을 보낸 사용자의 정보 가져오기
     */
    @Override
    @Transactional(readOnly = true)
    public Member getCurrentMember() {
        // JWT 토큰 -> Security Context의 Authenication -> Member id -> Member 엔티티 가져오기
        Long memberId = TokenUtil.getCurrentMemberId();

        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberReadResponseDto readMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        return new MemberReadResponseDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 10;
        Page<Member> memberPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");
        if(topId == 0) {
            memberPage = memberRepository.findAll(pageable);
        }
        else {
            memberPage = memberRepository.findAllByTopId(topId, pageable);
        }

        return new MemberReadAllResponseDto(memberPage.getContent(), memberPage.getPageable(), memberPage.isLast());
    }

    @Override
    public MemberUpdateResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        member.updateNickname(requestDto.getNickname());
        return new MemberUpdateResponseDto(memberRepository.save(member).getId());
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다. ")) ;
        memberRepository.delete(member);
    }
}
