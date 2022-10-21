package swm.wbj.asyncrum.domain.team;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.team.dto.*;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.team.exception.CodeAlreadyInUseException;
import swm.wbj.asyncrum.domain.team.exception.MemberAlreadyJoinedException;
import swm.wbj.asyncrum.domain.team.repository.TeamRepository;
import swm.wbj.asyncrum.domain.team.service.TeamServiceImpl;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;
import swm.wbj.asyncrum.domain.teammember.repository.TeamMemberRepository;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.mail.MailService;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.TeamRoleType;
import swm.wbj.asyncrum.global.utils.UrlService;

import java.util.List;
import java.util.Optional;


class TeamServiceImplTest {

    TeamRepository teamRepository = Mockito.mock(TeamRepository.class);
    TeamMemberRepository teamMemberRepository = Mockito.mock(TeamMemberRepository.class);
    MemberService memberService = Mockito.mock(MemberService.class);
    MailService mailService = Mockito.mock(MailService.class);
    UrlService urlService = Mockito.mock(UrlService.class);
    AwsService awsService = Mockito.mock(AwsService.class);

    @InjectMocks
    TeamServiceImpl teamService = new TeamServiceImpl(
            teamRepository, teamMemberRepository,
            memberService, mailService, urlService, awsService
    );

    static final Long MOCK_MEMBER_ID = 1L;
    static final Long MOCK_TEAM_ID = 1L;

    Team mockTeam = Mockito.mock(Team.class);
    Member mockMember = Mockito.mock(Member.class);
    TeamMember mockTeamMember = Mockito.mock(TeamMember.class);

    @BeforeEach
    void setUp() {
        Mockito.when(mockMember.getId()).thenReturn(MOCK_MEMBER_ID);
        Mockito.when(mockTeam.getId()).thenReturn(MOCK_TEAM_ID);
        Mockito.when(mockTeam.getName()).thenReturn("test team name");
        Mockito.when(mockTeamMember.getTeam()).thenReturn(mockTeam);
        Mockito.when(mockTeamMember.getMember()).thenReturn(mockMember);
        Mockito.when(mockTeamMember.getTeamRoleType()).thenReturn(TeamRoleType.OWNER);

        Mockito.when(memberService.getCurrentMember()).thenReturn(mockMember);
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockTeam));
        Mockito.when(teamMemberRepository.findByTeamAndMember(Mockito.any(), Mockito.any())).thenReturn(Optional.of(mockTeamMember));
    }

    @AfterEach
    void tearDown() { }

    @DisplayName("팀 생성")
    @Test
    void createTeam() {
        String name = "test team";
        String code = "test code";

        TeamCreateRequestDto requestDto = new TeamCreateRequestDto();
        requestDto.setName(name);
        requestDto.setCode(code);

        Mockito.when(teamRepository.existsByCode(Mockito.anyString())).thenReturn(false);
        Mockito.when(teamRepository.save(Mockito.any())).thenReturn(mockTeam);
        Mockito.when(teamMemberRepository.save(Mockito.any())).thenReturn(mockTeamMember);

        TeamCreateResponseDto createdTeam = teamService.createTeam(requestDto);

        Mockito.verify(teamRepository).existsByCode(code);
        Mockito.verify(teamRepository).save(Mockito.isA(Team.class));
        Mockito.verify(teamMemberRepository).save(Mockito.isA(TeamMember.class));

        Assertions.assertEquals(MOCK_TEAM_ID, createdTeam.getId());

        // Code 가 겹칠 때의 예외처리 검증
        Mockito.when(teamRepository.existsByCode(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(CodeAlreadyInUseException.class, () -> {
            teamService.createTeam(requestDto);
        });
    }

    @DisplayName("팀 멤버인지 확인 후 팀 가져오기")
    @Test
    void getTeamWithTeamMemberValidation() {
        Team returnedTeam = teamService.getTeamWithTeamMemberValidation(MOCK_TEAM_ID, mockMember);

        Mockito.verify(teamRepository).findById(MOCK_TEAM_ID);
        Mockito.verify(teamMemberRepository).findByTeamAndMember(mockTeam, mockMember);

        Assertions.assertEquals(mockTeam, returnedTeam);
    }

    @DisplayName("팀 오너인지 확인 후 팀 가져오기")
    @Test
    void getTeamWithOwnerValidation() {
        Team returnedTeam = teamService.getTeamWithOwnerValidation(MOCK_TEAM_ID, mockMember);

        Mockito.verify(teamRepository).findById(MOCK_TEAM_ID);
        Mockito.verify(teamMemberRepository).findByTeamAndMember(mockTeam, mockMember);

        Assertions.assertEquals(mockTeam, returnedTeam);

        // 오너가 아닐 때의 예외처리 검증
        TeamMember userMockTeamMember = mockTeamMember = new TeamMember() {
            @Override
            public TeamRoleType getTeamRoleType() {
                return TeamRoleType.USER;
            }
        };
        Mockito.when(teamMemberRepository.findByTeamAndMember(mockTeam, mockMember))
                .thenReturn(Optional.of(userMockTeamMember));

        Assertions.assertThrows(OperationNotAllowedException.class, () -> {
           teamService.getTeamWithOwnerValidation(MOCK_TEAM_ID, mockMember);
        });
    }

    @DisplayName("팀 가져오기")
    @Test
    void readTeam() {
        TeamReadResponseDto returnedTeam = teamService.readTeam(MOCK_TEAM_ID);

        Assertions.assertEquals(mockTeam.getId(), returnedTeam.getId());
    }

    @DisplayName("본인이 속해있는 팀 리스트 가져오기")
    @Test
    void readAllTeam() {
        long topId = 0L;
        int pageIndex = 0;
        int sizePerPage = 10;

        Page<TeamMember> teamPage = new PageImpl<>(List.of(mockTeamMember));

        // topId == 0 일 때 검증
        Mockito.when(teamMemberRepository.findAllByMember(Mockito.any(Member.class), Mockito.any(Pageable.class)))
                .thenReturn(teamPage);

        TeamReadAllResponseDto returnedTeamPage = teamService.readAllTeam(pageIndex, topId, sizePerPage);

        Mockito.verify(teamMemberRepository).findAllByMember(Mockito.isA(Member.class), Mockito.isA(Pageable.class));
        Assertions.assertEquals(MOCK_TEAM_ID, returnedTeamPage.getTeams().get(0).getId());

        // topId != 0 일 때 검증
        topId = 1L;
        Mockito.when(teamMemberRepository.findAllByMemberWithTopId(Mockito.any(Member.class), Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(teamPage);

        TeamReadAllResponseDto returnedTeamPageWithTopId = teamService.readAllTeam(pageIndex, topId, sizePerPage);

        Mockito.verify(teamMemberRepository).findAllByMember(Mockito.isA(Member.class), Mockito.isA(Pageable.class));
        Assertions.assertEquals(MOCK_TEAM_ID, returnedTeamPageWithTopId.getTeams().get(0).getId());
    }

    @DisplayName("팀 초대 이메일 발송")
    @Test
    void sendTeamInvitationLinkByEmail() throws Exception {
        String emailVerificationLink = "test emailVerificationLink";
        String memberEmail = "test email";

        Member requestMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID + 1;
            }

            @Override
            public String getEmail() {
                return memberEmail;
            }
        };

        TeamMemberAddRequestDto requestDto = new TeamMemberAddRequestDto();
        requestDto.setMemberEmail(memberEmail);

        Mockito.when(memberService.getUserByIdOrEmail(Mockito.isNull(), Mockito.anyString())).thenReturn(requestMember);
        Mockito.when(urlService.buildURL(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong())).thenReturn(emailVerificationLink);

        teamService.sendTeamInvitationLinkByEmail(MOCK_TEAM_ID, requestDto);

        Mockito.verify(memberService).getUserByIdOrEmail(null, requestMember.getEmail());
        Mockito.verify(urlService).buildURL("/api/v1/teams/" + MOCK_TEAM_ID + "/members/invitation", "memberId", requestMember.getId());
        Mockito.verify(mailService).sendTeamMemberInvitationLink(requestMember.getEmail(), emailVerificationLink, mockTeam.getName());
    }

    @DisplayName("팀 초대 이메일 검증 및 팀원으로 추가")
    @Test
    void verifyTeamInvitationLinkAndAddMember() {
        Member requestMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID + 1;
            }
        };

        Mockito.when(memberService.getUserByIdOrEmail(Mockito.anyLong(), Mockito.isNull())).thenReturn(requestMember);
        Mockito.when(teamMemberRepository.findByTeamAndMember(mockTeam, requestMember)).thenReturn(Optional.empty());

        teamService.verifyTeamInvitationLinkAndAddMember(MOCK_TEAM_ID, requestMember.getId());

        Mockito.verify(teamMemberRepository).findByTeamAndMember(mockTeam, requestMember);
        Mockito.verify(teamMemberRepository).save(Mockito.isA(TeamMember.class));

        // 이미 팀에 속해있을 때의 예외처리 검증
        Mockito.when(teamMemberRepository.findByTeamAndMember(mockTeam, requestMember)).thenReturn(Optional.of(mockTeamMember));

        Assertions.assertThrows(MemberAlreadyJoinedException.class, () -> {
            teamService.verifyTeamInvitationLinkAndAddMember(MOCK_TEAM_ID, requestMember.getId());
        });
    }

    @DisplayName("수동으로 멤버 추가")
    @Test
    void addMember() {
        Member requestMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID + 1;
            }
        };

        TeamMember mockRequestedTeamMember = new TeamMember() {
            @Override
            public Team getTeam() {
                return mockTeam;
            }

            @Override
            public Member getMember() {
                return requestMember;
            }
        };

        TeamMemberAddRequestDto requestDto = new TeamMemberAddRequestDto();
        requestDto.setMemberId(MOCK_MEMBER_ID + 1);

        Mockito.when(memberService.getUserByIdOrEmail(Mockito.anyLong(), Mockito.isNull())).thenReturn(requestMember);
        Mockito.when(teamMemberRepository.findByTeamAndMember(mockTeam, requestMember)).thenReturn(Optional.empty());
        Mockito.when(teamMemberRepository.save(Mockito.any())).thenReturn(mockRequestedTeamMember);

        TeamMemberAddResponseDto responseDto = teamService.addMember(MOCK_TEAM_ID, requestDto);

        Mockito.verify(teamMemberRepository).findByTeamAndMember(mockTeam, requestMember);
        Mockito.verify(teamMemberRepository).save(Mockito.isA(TeamMember.class));

        Assertions.assertEquals(mockTeam.getId(), responseDto.getTeamId());
        Assertions.assertEquals(requestMember.getId(), responseDto.getMemberId());
    }

    @DisplayName("멤버 팀에서 내보내기")
    @Test
    void removeMember() {
        Member removeMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_MEMBER_ID + 1;
            }
        };

        Mockito.when(memberService.getUserByIdOrEmail(Mockito.anyLong(), Mockito.isNull())).thenReturn(removeMember);
        Mockito.when(teamMemberRepository.findByTeamAndMember(mockTeam, removeMember)).thenReturn(Optional.of(mockTeamMember));

        teamService.removeMember(MOCK_TEAM_ID, removeMember.getId());

        Mockito.verify(teamMemberRepository).findByTeamAndMember(mockTeam, removeMember);
        Mockito.verify(mockTeam).removeMember(mockTeamMember);
        Mockito.verify(teamMemberRepository).delete(mockTeamMember);
    }

    @DisplayName("팀 정보 업데이트")
    @Test
    void updateTeam() {
        String updatedTeamName = "updated team name";

        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto();
        requestDto.setName(updatedTeamName);

        Mockito.when(teamRepository.save(Mockito.any())).thenReturn(mockTeam);

        TeamUpdateResponseDto responseDto = teamService.updateTeam(MOCK_TEAM_ID, requestDto);

        Mockito.verify(mockTeam).updateName(updatedTeamName);
        Assertions.assertEquals(mockTeam.getId(), responseDto.getId());
    }

    @DisplayName("팀 삭제")
    @Test
    void deleteTeam() {
        teamService.deleteTeam(MOCK_TEAM_ID);

        Mockito.verify(teamRepository).delete(mockTeam);
    }

    @DisplayName("팀 대표 사진 변경")
    @Test
    void createImage() {
        String imageFileKey = AwsService.IMAGE_TEAM_FILE_PREFIX + "_" + MOCK_TEAM_ID + "." + FileType.PNG.getName();
        String presignedUrl = "test presigned url";
        String imageFileUrl = "test image url";

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(presignedUrl);
        Mockito.when(awsService.getObjectURL(Mockito.anyString(), Mockito.anyString())).thenReturn(imageFileUrl);

        TeamImageCreateResponseDto responseDto = teamService.createImage(MOCK_TEAM_ID);

        Mockito.verify(awsService).generatePresignedURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME, FileType.PNG);
        Mockito.verify(awsService).getObjectURL(imageFileKey, AwsService.IMAGE_BUCKET_NAME);
        Mockito.verify(mockTeam).updateProfileImage(imageFileKey, imageFileUrl);

        Assertions.assertEquals(MOCK_TEAM_ID, responseDto.getId());
        Assertions.assertEquals(presignedUrl, responseDto.getPreSignedURL());
        Assertions.assertEquals(imageFileUrl, responseDto.getImageUrl());
    }
}