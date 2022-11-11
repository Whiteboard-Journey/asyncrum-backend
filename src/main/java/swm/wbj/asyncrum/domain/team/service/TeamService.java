package swm.wbj.asyncrum.domain.team.service;

import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.team.dto.*;
import swm.wbj.asyncrum.domain.team.entity.Team;


public interface TeamService {

    TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto);

    Team getTeamWithTeamMemberValidation(Long id, Member member);

    TeamReadResponseDto readTeam(Long id);

    Team getCurrentTeam(Long id);


    TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId, Integer SIZE_PER_PAGE);

    void sendTeamInvitationLinkByEmail(Long id,TeamMemberAddRequestDto requestDto) throws Exception;

    void verifyTeamInvitationLinkAndAddMember(Long id, Long memberId);

    TeamMemberAddResponseDto addMember(Long id, TeamMemberAddRequestDto requestDto);

    void removeMember(Long id, Long memberId);

    TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto);

    void deleteTeam(Long id);

    TeamImageCreateResponseDto createImage(Long id);
}
