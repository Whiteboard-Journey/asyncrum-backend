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
import swm.wbj.asyncrum.domain.userteam.team.repository.TeamRepository;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.utils.UrlService;

@RequiredArgsConstructor
@Transactional
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberService memberService;
    private final MailService mailService;
    private final UrlService urlService;

    // 팀 생성
    @Override
    public TeamCreateResponseDto createTeam(TeamCreateRequestDto requestDto) {
        Member member = memberService.getCurrentMember();
        String name = requestDto.getCode();

        // 팀만의 고유한 코드가 이미 존재한다면 예외처리
        if(teamRepository.existsByCode(name)) {
            throw new IllegalArgumentException("해당 코드는 이미 사용중입니다.");
        }

        Team team = requestDto.toEntity();
        team.addMember(member);
        return new TeamCreateResponseDto(teamRepository.save(team).getId());
    }

    // 단일 팀 조회
    @Override
    @Transactional(readOnly = true)
    public TeamReadResponseDto readTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        return new TeamReadResponseDto(team);
    }

    // 팀 전체 조회
    @Override
    @Transactional(readOnly = true)
    public TeamReadAllResponseDto readAllTeam(Integer pageIndex, Long topId) {
        int SIZE_PER_PAGE = 12;

        Page<Team> teamPage;
        Pageable pageable = PageRequest.of(pageIndex, SIZE_PER_PAGE, Sort.Direction.DESC, "id");

        if(topId == 0) {
            teamPage = teamRepository.findAll(pageable);
        }
        else {
            teamPage = teamRepository.findAllByTopId(topId, pageable);
        }

        return new TeamReadAllResponseDto(teamPage.getContent(), teamPage.getPageable(), teamPage.isLast());
    }

    // 팀원 추가: 초대 링크 방식
    @Override
    public void sendTeamInvitationLinkByEmail(Long id, TeamMemberAddRequestDto requestDto) throws Exception {
        // 요청자가 해당 팀의 속해 있는지 검증
        // TODO: 중복되는 검증 코드 분리하기, 본인 제외하는 검증 추가
        Member requestMember = memberService.getCurrentMember();
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        if(!team.getMembers().contains(requestMember)) {
            throw new IllegalArgumentException("해당 팀에 팀원을 추가할 수 있는 권한이 없습니다.");
        }

        // 요청 이메일을 통해 멤버 정보 가져온 후 해당 멤버의 이메일로 초대 링크 발송
        Member member = memberService.getUserByIdOrEmail(null, requestDto.getMemberEmail());
        String emailVerificationLink = urlService.buildURL("/api/v1/teams/" + id + "/members/invitation", "memberId", member.getId());
        mailService.sendTeamMemberInvitationLink(member.getEmail(), emailVerificationLink, team.getName());
    }

    // 팀원 초대 링크 검증 (JWT 토큰 없이 가능)
    @Override
    public void verifyTeamInvitationLinkAndAddMember(Long id, Long memberId) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));
        Member member = memberService.getUserByIdOrEmail(memberId, null);

        team.addMember(member);
        teamRepository.save(team);
    }

    // 팀원 추가: 수동 방식
    @Override
    public TeamMemberAddResponseDto addMember(Long id, TeamMemberAddRequestDto requestDto) {
        // 요청자가 해당 팀의 속해 있는지 검증
        Member requestMember = memberService.getCurrentMember();
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        if(!team.getMembers().contains(requestMember)) {
            throw new IllegalArgumentException("해당 팀에 팀원을 추가할 수 있는 권한이 없습니다.");
        }

        // 추가할 팀원 정보 가져온 후 팀에 추가
        Member member = memberService.getUserByIdOrEmail(requestDto.getMemberId(), null);
        team.addMember(member);

        return new TeamMemberAddResponseDto(id, member.getId());
    }

    // 팀원 삭제
    @Override
    public void removeMember(Long id, Long memberId) {
        // 요청자와 제거할 팀원 모두가 해당 팀의 속해 있는지 검증
        Member requestMember = memberService.getCurrentMember();
        Member member = memberService.getUserByIdOrEmail(memberId, null);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        if(!team.getMembers().contains(requestMember)) {
            throw new IllegalArgumentException("해당 팀에 팀원을 제거할 수 있는 권한이 없습니다.");
        }
        if(!team.getMembers().contains(member)) {
            throw new IllegalArgumentException("해당 팀에 해당 팀원이 없습니다.");
        }

        // 해당 팀원 팀에서 제거
        team.removeMember(member);
    }

    // 팀 정보 업데이트
    @Override
    public TeamUpdateResponseDto updateTeam(Long id, TeamUpdateRequestDto requestDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        team.update(requestDto.getName(), requestDto.getPictureUrl());

        return new TeamUpdateResponseDto(teamRepository.save(team).getId());
    }

    // 팀 삭제
    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        teamRepository.delete(team);
    }

}
