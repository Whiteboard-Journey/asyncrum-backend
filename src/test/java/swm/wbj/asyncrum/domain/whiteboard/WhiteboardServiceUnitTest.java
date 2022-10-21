package swm.wbj.asyncrum.domain.whiteboard;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.team.service.TeamService;
import swm.wbj.asyncrum.domain.team.service.TeamServiceImpl;
import swm.wbj.asyncrum.domain.whiteboard.dto.WhiteboardCreateRequestDto;
import swm.wbj.asyncrum.domain.whiteboard.dto.WhiteboardUpdateRequestDto;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;
import swm.wbj.asyncrum.domain.whiteboard.exception.WhiteboardNotExistsException;
import swm.wbj.asyncrum.domain.whiteboard.repository.WhiteboardRepository;
import swm.wbj.asyncrum.domain.whiteboard.service.WhiteboardServiceImpl;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.Optional;

public class WhiteboardServiceUnitTest {

    WhiteboardRepository whiteboardRepository = Mockito.mock(WhiteboardRepository.class);

    MemberService memberService = Mockito.mock(MemberServiceImpl.class);

    TeamService teamService = Mockito.mock(TeamServiceImpl.class);

    AwsService awsService = Mockito.mock(AwsService.class);

    @InjectMocks
    WhiteboardServiceImpl whiteboardService = new WhiteboardServiceImpl(whiteboardRepository, memberService, teamService, awsService);

    static final Long MOCK_ID = 1L;
    static final String MOCK_FILE_KEY = "FILE KEY";
    Whiteboard mockWhiteboard;
    Member mockMember;
    Team mockTeam;

    @BeforeEach
    void setUp(){
        mockWhiteboard = new Whiteboard() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }

            @Override
            public Member getMember() {
                return mockMember;
            }

            @Override
            public Team getTeam() {
                return mockTeam;
            }

            @Override
            public String getWhiteboardFileKey() {
                return MOCK_FILE_KEY;
            }
        };

        mockMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };

        mockTeam = new Team(){
            @Override
            public Long getId(){
                return MOCK_ID;
            }
        };

        Mockito.when(memberService.getCurrentMember()).thenReturn(mockMember);
        Mockito.when(teamService.getTeamWithTeamMemberValidation(Mockito.anyLong(), Mockito.any(Member.class))).thenReturn(mockTeam);
        Mockito.when(whiteboardRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockWhiteboard));
    }

    @AfterEach
    void tearDown(){}

    @DisplayName("화이트보드 생성")
    @Test
    void createWhiteboard(){
        String preSignedURL = "preSigned URL";
        String title = "title";
        String description = "description";
        String scope = "TEAM";

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(preSignedURL);
        Mockito.when(whiteboardRepository.save(Mockito.any(Whiteboard.class))).thenReturn(mockWhiteboard);

        WhiteboardCreateRequestDto requestDto= new WhiteboardCreateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(scope);
        requestDto.setTeamId(MOCK_ID);

        whiteboardService.createWhiteboard(requestDto);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Mockito.verify(whiteboardRepository).save(Mockito.isA(Whiteboard.class));
        Mockito.verify(awsService).generatePresignedURL(Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(FileType.class));
    }

    @DisplayName("현재 화이트보드 가져오기")
    @Test
    void getCurrentWhiteBoard() {
        // 정상 처리 검증
        whiteboardService.getCurrentWhiteboard(MOCK_ID);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(whiteboardRepository).findById(MOCK_ID);
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Assertions.assertEquals(whiteboardService.getCurrentWhiteboard(MOCK_ID), mockWhiteboard);

        // 화이트보드가 없을 때  예외 처리 검증
        Mockito.when(whiteboardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WhiteboardNotExistsException.class, () -> {
            whiteboardService.getCurrentWhiteboard(MOCK_ID + 1L);
        });
    }

    @DisplayName("범위에 따라 화이트보드 리스트 가져오기")
    @Test
    void readAllWhiteboard() {
        ScopeType scope;
        Integer pageIndex = 0;
        Long topId = 0L;
        Integer sizePerPage = 12;

        Mockito.when(whiteboardRepository.findAllByTeam(Mockito.any(Team.class), Mockito.any(Member.class), Mockito.any(Pageable.class))).thenReturn(Page.empty());
        Mockito.when(whiteboardRepository.findAllByTeamAndMember(Mockito.any(Team.class), Mockito.any(Member.class), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        // 범위가 TEAM 일 때 검증
        scope = ScopeType.TEAM;

        whiteboardService.readAllWhiteboard(MOCK_ID, scope, pageIndex, topId, sizePerPage);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Mockito.verify(whiteboardRepository).findAllByTeam(Mockito.isA(Team.class), Mockito.isA(Member.class), Mockito.isA(Pageable.class));

        // 범위가 PRIVATE 일 때 검증
        scope = ScopeType.PRIVATE;

        whiteboardService.readAllWhiteboard(MOCK_ID, scope, pageIndex, topId, sizePerPage);

        Mockito.verify(whiteboardRepository).findAllByTeamAndMember(Mockito.isA(Team.class), Mockito.isA(Member.class), Mockito.isA(Pageable.class));
    }


    @DisplayName("화이트보드 정보 업데이트")
    @Test
    void updateWhiteboard() {
        String preSignedURL = "preSigned URL";
        String title = "updated title";
        String description = "updated description";
        String scope = "PRIVATE";

        WhiteboardUpdateRequestDto requestDto = new WhiteboardUpdateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(scope);

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(preSignedURL);
        Mockito.when(whiteboardRepository.save(Mockito.any(Whiteboard.class))).thenReturn(mockWhiteboard);

        // 정상 처리 검증
        whiteboardService.updateWhiteboard(MOCK_ID, requestDto);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(whiteboardRepository).findById(MOCK_ID);
        Mockito.verify(awsService).generatePresignedURL(MOCK_FILE_KEY, AwsService.WHITEBOARD_BUCKET_NAME, FileType.TLDR);
        Assertions.assertEquals(mockWhiteboard.getTitle(), title);
        Assertions.assertEquals(mockWhiteboard.getDescription(), description);
        Assertions.assertEquals(mockWhiteboard.getScope(), ScopeType.PRIVATE);

        // 현재 멤버가 화이트보드를 소유하고 있지 않을 경우 예외 검증
        Member notOwnedMember = new Member();
        Mockito.when(memberService.getCurrentMember()).thenReturn(notOwnedMember);

        Assertions.assertThrows(OperationNotAllowedException.class, () -> {
            whiteboardService.updateWhiteboard(MOCK_ID, requestDto);
        });
    }


    @DisplayName("화이트보드 삭제")
    @Test
    void deleteWhiteboard() {
        // 정상 처리 검증
        whiteboardService.deleteWhiteboard(MOCK_ID);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(whiteboardRepository).findById(MOCK_ID);
        Mockito.verify(awsService).deleteFile(MOCK_FILE_KEY, AwsService.WHITEBOARD_BUCKET_NAME);
        Mockito.verify(whiteboardRepository).delete(mockWhiteboard);

        // 화이트보드가 없을 때 예외 처리 검증
        Mockito.when(whiteboardRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WhiteboardNotExistsException.class, () -> {
            whiteboardService.deleteWhiteboard(MOCK_ID + 1L);
        });
    }






}
