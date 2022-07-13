package swm.wbj.asyncrum.domain.userteam.member.service;

import swm.wbj.asyncrum.domain.userteam.member.dto.MemberCreateRequestDto;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberReadAllResponseDto;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberReadResponseDto;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberUpdateRequestDto;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamReadAllResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamUpdateRequestDto;


public interface MemberService {
    Long createMember(MemberCreateRequestDto requestDto);
    MemberReadResponseDto readMember(Long id) throws Exception;
    MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId);
    Long updateMember(Long id, MemberUpdateRequestDto requestDto);
    void deleteMember(Long id);
}
