package swm.wbj.asyncrum.domain.userteam.member.service;

import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamReadAllResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamUpdateRequestDto;


public interface MemberService {

    MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto);
    MemberReadResponseDto readMember(Long id);
    MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId);
    MemberUpdateResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto);
    void deleteMember(Long id);

}
