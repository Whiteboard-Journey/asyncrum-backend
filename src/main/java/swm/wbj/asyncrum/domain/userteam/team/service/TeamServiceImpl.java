package swm.wbj.asyncrum.domain.userteam.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.team.dto.*;
import swm.wbj.asyncrum.domain.userteam.team.exception.*;
import swm.wbj.asyncrum.domain.userteam.team.repository.TeamRepository;
import swm.wbj.asyncrum.domain.userteam.teammember.entity.TeamMember;
import swm.wbj.asyncrum.domain.userteam.teammember.repository.TeamMemberRepository;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.TeamRoleType;
import swm.wbj.asyncrum.global.utils.UrlService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static swm.wbj.asyncrum.global.media.AwsService.IMAGE_BUCKET_NAME;
import static swm.wbj.asyncrum.global.media.AwsService.IMAGE_TEAM_FILE_PREFIX;

@RequiredArgsConstructor
@Transactional
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberService memberService;
    private final MailService mailService;
    private final UrlService urlService;
    private final AwsService awsService;

    @Override
    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto) {
        Member member = memberService.getCurrentMember();

        if(teamCodeAlreadyExists(requestDto.getCode())) {
            throw new CodeAlreadyInUseException();
        }

        Team team = teamRepository.save(requestDto.toEntity());
        TeamMember teamMember = TeamMember.createTeamMember()
                                            .team(team)
                                            .member(member)
                                            .teamRoleType(TeamRoleType.OWNER)
                                            .build();

        return new TeamCreateResponseDto(teamMemberRepository.save(teamMember).getTeam().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeamWithTeamMemberValidation(Long id, Member member) {
        Team team = teamRepository.findById(id)
                .orElseThrow(TeamNotExistsException::new);

        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(team, member)
                .orElseThrow(MemberNotInTeamException::new);

        return teamMember.getTeam();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamReadResponseDto readTeam(Long id) {
        Member currentMember = memberService.getCurrentMember();

        return new TeamReadResponseDto(getTeamWithTeamMemberValidation(id, currentMember));
    }

    @Override
    @Transactional(readOnly = true)
    public TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId, Integer sizePerPage) {
        Member currentMember = memberService.getCurrentMember();

        Page<TeamMember> teamPage;
        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.Direction.DESC, "id");

        if(topId == 0) {
            teamPage = teamMemberRepository.findAllByMember(currentMember, pageable);
        }
        else {
            teamPage = teamMemberRepository.findAllByMemberWithTopId(currentMember, topId, pageable);
        }

        return new TeamReadAllResponseDto(teamPage.getContent(), teamPage.getPageable(), teamPage.isLast());
    }

    @Override
    public void sendTeamInvitationLinkByEmail(Long id, TeamMemberAddRequestDto requestDto) throws Exception {
        Member requestMember = memberService.getCurrentMember();
        Team team = getTeamWithOwnerValidation(id, requestMember);

        Member member = memberService.getUserByIdOrEmail(null, requestDto.getMemberEmail());
        String emailVerificationLink = urlService.buildURL(
                "/api/v1/teams/" + id + "/members/invitation", "memberId", member.getId());

        mailService.sendTeamMemberInvitationLink(member.getEmail(), emailVerificationLink, team.getName());
    }

    @Override
    public void verifyTeamInvitationLinkAndAddMember(Long id, Long memberId) {
        Team team = teamRepository.findById(id)
                .orElseThrow(TeamNotExistsException::new);
        Member requestMember = memberService.getUserByIdOrEmail(memberId, null);

        if (isMemberAlreadyInTeam(team, requestMember)) {
            throw new MemberAlreadyJoinedException();
        }

        TeamMember teamMember = TeamMember.createTeamMember()
                                            .team(team)
                                            .member(requestMember)
                                            .teamRoleType(TeamRoleType.USER)
                                            .build();

        teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMemberAddResponseDto addMember(Long id, TeamMemberAddRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Team team = getTeamWithTeamMemberValidation(id, currentMember);
        Member requestMember = memberService.getUserByIdOrEmail(requestDto.getMemberId(), null);

        if (isMemberAlreadyInTeam(team, requestMember)) {
            throw new MemberAlreadyJoinedException();
        }

        TeamMember teamMember = TeamMember.createTeamMember()
                .team(team)
                .member(requestMember)
                .teamRoleType(TeamRoleType.USER)
                .build();

        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);
        return new TeamMemberAddResponseDto(savedTeamMember.getTeam().getId(), savedTeamMember.getMember().getId());
    }

    @Override
    public TeamUpdateResponseDto addRoomName(Long id, TeamMeetingRequestDto requestDto) {
        Team team = getTeam(id).orElseThrow(TeamNotExistsException::new);

        Set<String> openMeetings = Optional.of(team.getOpenMeeting()).orElse(new HashSet<>());

        //team의 openMeeting에 이미 이름이 있는 경우
        if (openMeetings.contains(requestDto.getRoomName())) {
            throw new RoomNameAlreadyException();
        }
        // roomName
        openMeetings.add(requestDto.getRoomName());
        team.updateOpenMeeting(openMeetings);

        return new TeamUpdateResponseDto(teamRepository.save(team).getId());
    }

    @Override
    public void removeRoomName(Long id, TeamMeetingRequestDto requestDto) {
        Team team = getTeam(id).orElseThrow(TeamNotExistsException::new);
        Set<String> openMeetings = Optional.of(team.getOpenMeeting()).orElse(new HashSet<>());
        if (!openMeetings.contains(requestDto.getRoomName())) {
            throw new RoomNameNotExistsException();
        }
        openMeetings.remove(requestDto.getRoomName());
        team.updateOpenMeeting(openMeetings);
    }

    @Override
    public void removeMember(Long id, Long memberId) {
        Member requestMember = memberService.getCurrentMember();
        Member removeMember = memberService.getUserByIdOrEmail(memberId, null);
        Team team = getTeamWithOwnerValidation(id, requestMember);

        TeamMember removeTeamMember = teamMemberRepository.findByTeamAndMember(team, removeMember)
                .orElseThrow(MemberNotInTeamException::new);

        team.removeMember(removeTeamMember);
        teamMemberRepository.delete(removeTeamMember);
    }

    @Override
    public TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto) {
        Member currentMember = memberService.getCurrentMember();
        Team team = getTeamWithOwnerValidation(id, currentMember);

        team.updateName(requestDto.getName());

        return new TeamUpdateResponseDto(teamRepository.save(team).getId());
    }

    @Override
    public void deleteTeam(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Team team = getTeamWithOwnerValidation(id, currentMember);

        teamRepository.delete(team);
    }

    @Override
    public TeamImageCreateResponseDto createImage(Long id) {
        Member currentMember = memberService.getCurrentMember();
        Team team = getTeamWithOwnerValidation(id, currentMember);

        String imageFileKey = createImageFileKey(team.getId());
        String preSignedURL = awsService.generatePresignedURL(imageFileKey, IMAGE_BUCKET_NAME, FileType.PNG);
        String imageUrl = awsService.getObjectURL(imageFileKey, IMAGE_BUCKET_NAME);

        team.updateProfileImage(imageFileKey, imageUrl);

        return new TeamImageCreateResponseDto(id, preSignedURL, imageUrl);
    }

    private boolean isMemberAlreadyInTeam(Team team, Member requestMember) {
        return teamMemberRepository.findByTeamAndMember(team, requestMember).isPresent();
    }


    private Team getTeamWithOwnerValidation(Long id, Member requestMember) {
        Team team = teamRepository.findById(id)
                .orElseThrow(TeamNotExistsException::new);

        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(team, requestMember)
                .orElseThrow(MemberNotInTeamException::new);

        if(!hasOwnerRole(teamMember)) {
            throw new OperationNotAllowedException();
        }

        return team;
    }

    private Optional<Team> getTeam(Long id) {
        return teamRepository.findById(id);
    }


    private Boolean teamCodeAlreadyExists(String code) {
        return teamRepository.existsByCode(code);
    }

    private boolean hasOwnerRole(TeamMember teamMember) {
        return teamMember.getTeamRoleType().equals(TeamRoleType.OWNER);
    }

    public String createImageFileKey(Long memberId) {
        return IMAGE_TEAM_FILE_PREFIX + "_" + memberId + "." + FileType.PNG.getName();
    }
}
