package swm.wbj.asyncrum.domain.record;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.record.record.dto.RecordCreateRequestDto;
import swm.wbj.asyncrum.domain.record.record.dto.RecordUpdateRequestDto;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.record.record.exception.RecordNotExistsException;
import swm.wbj.asyncrum.domain.record.record.repository.RecordRepository;
import swm.wbj.asyncrum.domain.record.record.service.RecordServiceImpl;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.service.MemberService;
import swm.wbj.asyncrum.domain.member.service.MemberServiceImpl;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.team.service.TeamService;
import swm.wbj.asyncrum.domain.team.service.TeamServiceImpl;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;
import swm.wbj.asyncrum.global.type.ScopeType;

import java.util.Optional;

class RecordServiceUnitTest {

    RecordRepository recordRepository = Mockito.mock(RecordRepository.class);

    MemberService memberService = Mockito.mock(MemberServiceImpl.class);

    TeamService teamService = Mockito.mock(TeamServiceImpl.class);

    AwsService awsService = Mockito.mock(AwsService.class);

    @InjectMocks
    RecordServiceImpl recordService = new RecordServiceImpl(recordRepository, memberService, teamService, awsService);

    static final Long MOCK_ID = 1L;
    static final String MOCK_FILE_KEY = "FILE KEY";
    Record mockRecord;
    Member mockMember;
    Team mockTeam;

    @BeforeEach
    void setUp() {
        mockRecord = new Record() {
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
            public String getRecordFileKey() {
                return MOCK_FILE_KEY;
            }
        };

        mockMember = new Member() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };

        mockTeam = new Team() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }
        };

        Mockito.when(memberService.getCurrentMember()).thenReturn(mockMember);
        Mockito.when(teamService.getTeamWithTeamMemberValidation(Mockito.anyLong(), Mockito.any(Member.class))).thenReturn(mockTeam);
        Mockito.when(recordRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockRecord));
    }

    @AfterEach
    void tearDown() { }

    @DisplayName("녹화 생성")
    @Test
    void createRecord() {
        String preSignedURL = "preSigned URL";
        String title = "title";
        String description = "description";
        String scope = "TEAM";

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(preSignedURL);
        Mockito.when(recordRepository.save(Mockito.any(Record.class))).thenReturn(mockRecord);

        RecordCreateRequestDto requestDto = new RecordCreateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(scope);
        requestDto.setTeamId(MOCK_ID);

        // 정상 처리 검증
        recordService.createRecord(requestDto);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Mockito.verify(recordRepository).save(Mockito.isA(Record.class));
        Mockito.verify(awsService).generatePresignedURL(Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(FileType.class));
    }

    @DisplayName("현재 녹화 가져오기")
    @Test
    void getCurrentRecord() {
        // 정상 처리 검증
        recordService.getCurrentRecord(MOCK_ID);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(recordRepository).findById(MOCK_ID);
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Assertions.assertEquals(recordService.getCurrentRecord(MOCK_ID), mockRecord);

        // 녹화가 없을 때 예외 처리 검증
        Mockito.when(recordRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotExistsException.class, () -> {
            recordService.getCurrentRecord(MOCK_ID + 1L);
        });
    }

    @DisplayName("범위에 따라 녹화 리스트 가져오기")
    @Test
    void readAllRecord() {
        ScopeType scope;
        Integer pageIndex = 0;
        Long topId = 0L;
        Integer sizePerPage = 12;

        Mockito.when(recordRepository.findAllByTeam(Mockito.any(Team.class), Mockito.any(Member.class), Mockito.any(Pageable.class))).thenReturn(Page.empty());
        Mockito.when(recordRepository.findAllByTeamAndMember(Mockito.any(Team.class), Mockito.any(Member.class), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        // 범위가 TEAM 일 때 검증
        scope = ScopeType.TEAM;

        recordService.readAllRecord(MOCK_ID, scope, pageIndex, topId, sizePerPage);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(teamService).getTeamWithTeamMemberValidation(mockTeam.getId(), mockMember);
        Mockito.verify(recordRepository).findAllByTeam(Mockito.isA(Team.class), Mockito.isA(Member.class), Mockito.isA(Pageable.class));

        // 범위가 PRIVATE 일 때 검증
        scope = ScopeType.PRIVATE;

        recordService.readAllRecord(MOCK_ID, scope, pageIndex, topId, sizePerPage);

        Mockito.verify(recordRepository).findAllByTeamAndMember(Mockito.isA(Team.class), Mockito.isA(Member.class), Mockito.isA(Pageable.class));
    }

    @DisplayName("녹화 정보 업데이트")
    @Test
    void updateRecord() {
        String preSignedURL = "preSigned URL";
        String title = "updated title";
        String description = "updated description";
        String scope = "PRIVATE";

        RecordUpdateRequestDto requestDto = new RecordUpdateRequestDto();
        requestDto.setTitle(title);
        requestDto.setDescription(description);
        requestDto.setScope(scope);

        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(preSignedURL);
        Mockito.when(recordRepository.save(Mockito.any(Record.class))).thenReturn(mockRecord);

        // 정상 처리 검증
        recordService.updateRecord(MOCK_ID, requestDto);

        Mockito.verify(memberService).getCurrentMember();

        Mockito.verify(recordRepository).findById(MOCK_ID);
        Mockito.verify(awsService).generatePresignedURL(MOCK_FILE_KEY, AwsService.RECORD_BUCKET_NAME, FileType.MP4);
        Assertions.assertEquals(mockRecord.getTitle(), title);
        Assertions.assertEquals(mockRecord.getDescription(), description);
        Assertions.assertEquals(mockRecord.getScope(), ScopeType.PRIVATE);

        // 현재 멤버가 녹화를 소유하고 있지 않을 경우 예외 검증
        Member notOwnedMember = new Member();
        Mockito.when(memberService.getCurrentMember()).thenReturn(notOwnedMember);
        Mockito.when(teamService.getTeamWithTeamMemberValidation(MOCK_ID, notOwnedMember)).thenReturn(null);

        Assertions.assertThrows(OperationNotAllowedException.class, () -> {
            recordService.updateRecord(MOCK_ID, requestDto);
        });
    }

    @DisplayName("녹화 삭제")
    @Test
    void deleteRecord() {
        // 정상 처리 검증
        recordService.deleteRecord(MOCK_ID);

        Mockito.verify(memberService).getCurrentMember();
        Mockito.verify(recordRepository).findById(MOCK_ID);
        Mockito.verify(awsService).deleteFile(MOCK_FILE_KEY, AwsService.RECORD_BUCKET_NAME);
        Mockito.verify(recordRepository).delete(mockRecord);

        // 녹화가 없을 때 예외 처리 검증
        Mockito.when(recordRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RecordNotExistsException.class, () -> {
            recordService.deleteRecord(MOCK_ID + 1L);
        });
    }
}