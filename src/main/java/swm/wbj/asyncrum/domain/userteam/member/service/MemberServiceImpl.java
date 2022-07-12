package swm.wbj.asyncrum.domain.userteam.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public List<MemberDto> getListAll() {
        return memberRepository.findAll().stream().map(entity -> entityToDto(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void register(MemberDto dto) {
        Member member = memberRepository.save(dtoToEntity(dto)); //dto -> dao -> repository -> db

    }


    @Override
    public void delete(MemberDto dto) {
        memberRepository.delete(dtoToEntity(dto));
    }

    @Override
    public Optional<Member> read(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member;
    }

    @Override
    public Long patch(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("멤버를 찾을 수 없습니다"));
        member.update(memberDto.getName(), memberDto.getEmail(), memberDto.getPictureUrl(), memberDto.getPhone(), memberDto.getNickname());
        return memberRepository.save(member).getId();
    }

}
