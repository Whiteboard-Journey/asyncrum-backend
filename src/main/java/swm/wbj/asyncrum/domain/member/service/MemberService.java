package swm.wbj.asyncrum.domain.member.service;

import swm.wbj.asyncrum.domain.member.dto.*;
import swm.wbj.asyncrum.domain.member.entity.Member;

import java.io.IOException;

public interface MemberService {

    MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto);
    Member getCurrentMember();
    Member getMemberByIdOrEmail(Long id, String email);
    Member getMemberByFullname(String fullname);
    MemberReadResponseDto readMember(Long id);
    MemberReadAllResponseDto readAllMember(Integer pageIndex, Long topId, Integer sizePerPage);
    MemberUpdateResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto);
    void deleteMember(Long id);
    void sendEmailVerificationLinkByEmail() throws Exception;
    void verifyEmailVerificationLink(Long memberId) throws Exception;
    ImageCreateResponseDto createImage(Long id) throws IOException;
}
