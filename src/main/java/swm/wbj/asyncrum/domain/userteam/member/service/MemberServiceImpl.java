package swm.wbj.asyncrum.domain.userteam.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public List<Member> readAllMember() {
        return memberRepository.findAll();
    }

    @Override
    public void createMember(MemberDto dto) {
        memberRepository.save(dtoToEntity(dto)); //dto -> dao -> repository -> db

    }


    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public Member readMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        return member;
    }

    @Override
    public Long updateMember(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        member.update(memberDto.getPhone(), memberDto.getNickname());
        return memberRepository.save(member).getId();
    }

}
