package swm.wbj.asyncrum.domain.userteam.member.service;

import swm.wbj.asyncrum.domain.userteam.member.dto.MemberDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

import java.util.List;
import java.util.Optional;


public interface MemberService {

    List<MemberDto> getListAll();
    void register(MemberDto memberDto);
    void delete(MemberDto memberDto);
    Optional<Member> read(Long id);
    Long patch(Long id, MemberDto memberDto);




    default Member dtoToEntity(MemberDto dto) {
        Member entity = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .pictureUrl(dto.getPictureUrl())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .build();

        return entity;
    }

    default MemberDto entityToDto(Member entity){
        MemberDto dto = MemberDto.builder()
                .name(entity.getName())
                .email(entity.getEmail())
                .pictureUrl(entity.getPictureUrl())
                .phone(entity.getPhone())
                .nickname(entity.getNickname())
                .build();
        return dto;
    }



}
