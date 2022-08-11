package swm.wbj.asyncrum.domain.userteam.team.service;

import swm.wbj.asyncrum.domain.userteam.team.dto.*;

import java.io.IOException;

public interface TeamService {

    // 팀 생성
    TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto);

    // 단일 팀 조회
    TeamReadResponseDto readTeam(Long id);

    // 팀 전체 조회
    TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId);

    // 팀원 추가: 초대 링크 방식
    void sendTeamInvitationLinkByEmail(Long id,TeamMemberAddRequestDto requestDto) throws Exception;

    // 팀원 초대 링크 검증
    void verifyTeamInvitationLinkAndAddMember(Long id, Long memberId);

    // 팀원 추가: 수동 방식
    TeamMemberAddResponseDto addMember(Long id, TeamMemberAddRequestDto requestDto);

    // 팀원 삭제
    void removeMember(Long id, Long memberId);

    // 팀 정보 업데이트
    TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto);

    // 팀 삭제
    void deleteTeam(Long id);

    TeamImageCreateResponseDto createImage(Long id) throws IOException;

}
