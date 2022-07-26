package swm.wbj.asyncrum.domain.userteam.member.service;

import swm.wbj.asyncrum.domain.userteam.member.dto.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamReadAllResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.dto.TeamUpdateRequestDto;


public interface MemberService {

    MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto);
    Member getCurrentMember();
    Member getUserByIdOrEmail(Long id, String email);
    MemberReadResponseDto readMember(Long id);
    MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId);
    MemberUpdateResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto);
    void deleteMember(Long id);
    void sendEmailVerificationLinkByEmail() throws Exception;
    void verifyEmailVerificationLink(Long memberId) throws Exception;
}
