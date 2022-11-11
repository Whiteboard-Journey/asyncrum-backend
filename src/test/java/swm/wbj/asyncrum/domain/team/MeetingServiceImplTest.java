package swm.wbj.asyncrum.domain.team;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.meeting.dto.MeetingCreateRequestDto;
import swm.wbj.asyncrum.domain.meeting.dto.MeetingUpdateRequestDto;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.meeting.exception.MeetingNotExistsException;
import swm.wbj.asyncrum.domain.meeting.repository.MeetingRepository;
import swm.wbj.asyncrum.domain.meeting.service.MeetingServiceImpl;
import swm.wbj.asyncrum.domain.team.service.TeamService;
import swm.wbj.asyncrum.domain.team.service.TeamServiceImpl;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.mockito.Mockito.times;

class MeetingServiceImplTest {

    MeetingRepository meetingRepository = Mockito.mock(MeetingRepository.class);

    TeamService teamService = Mockito.mock(TeamServiceImpl.class);

    MemberRepository memberRepository = Mockito.mock(MemberRepository.class);

    AwsService awsService = Mockito.mock(AwsService.class);

    @InjectMocks
    MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingRepository, teamService, memberRepository, awsService);

    static final Long MOCK_ID = 1L;

    static final String MOCK_FILE_KEY = "FILE KEY";

    Meeting mockMeeting;
    Member mockMember;
    Team mockTeam;

    @BeforeEach
    void setUp() {
        mockMeeting = new Meeting(){
            @Override
            public Long getId() {return MOCK_ID;}

            @Override
            public Team getTeam() { return mockTeam;}

            @Override
            public String getMeetingFileKey() {
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
        Mockito.when(teamService.getCurrentTeam(MOCK_ID)).thenReturn(mockTeam);
        Mockito.when(meetingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockMeeting));

    }

    @DisplayName("미팅 생성")
    @Test
    void createMeeting() {
        Long teamId = MOCK_ID;
        String meetingName = "add test";

        Mockito.when(meetingRepository.save(Mockito.any(Meeting.class))).thenReturn(mockMeeting);

        MeetingCreateRequestDto requestDto = new MeetingCreateRequestDto();
        requestDto.setTeamId(MOCK_ID);
        requestDto.setMeetingName(meetingName);

        meetingService.createMeeting(requestDto);

        Mockito.verify(meetingRepository, times(2)).save(Mockito.isA(Meeting.class));

        Mockito.when(meetingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockMeeting));


    }

    @DisplayName("미팅 녹화 파일 생성")
    @Test
    void createMeetingFile(){

        String preSignedURL = "preSigned URL";
        Mockito.when(awsService.generatePresignedURL(Mockito.anyString(), Mockito.anyString(), Mockito.any(FileType.class))).thenReturn(preSignedURL);
        meetingService.createMeetingFile(MOCK_ID);
        Mockito.verify(awsService).generatePresignedURL(Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(FileType.class));

    }


    @DisplayName("현재 미팅 가져오기")
    @Test
    void readMeeting() {
        meetingService.readMeeting(MOCK_ID);

        Mockito.verify(meetingRepository).findById(MOCK_ID);
        Mockito.when(meetingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(MeetingNotExistsException.class, () -> {
            meetingService.readMeeting(MOCK_ID + 1L);
        });
    }

    @DisplayName("범위에 따라 미팅 리스트 가져오기")
    @Test
    void readAllMeeting() {
        meetingService.readAllMeeting(MOCK_ID);

        Mockito.verify(meetingRepository).findAllByTeam(mockTeam);
    }

    @DisplayName("미팅 정보 업데이트")
    @Test
    void updateMeeting() {

        Boolean status = true;


        MeetingUpdateRequestDto requestDto = new MeetingUpdateRequestDto();
        requestDto.setStatus(status);

        Mockito.when(meetingRepository.save(Mockito.any(Meeting.class))).thenReturn(mockMeeting);

        meetingService.updateMeeting(MOCK_ID, requestDto);
        Mockito.verify(meetingRepository).findById(MOCK_ID);

        Assertions.assertEquals(mockMeeting.getStatus(), status);

    }

    @DisplayName("미팅 삭제")
    @Test
    void deleteMeeting() {

        meetingService.deleteMeeting(MOCK_ID);

        Mockito.verify(meetingRepository).findById(MOCK_ID);
        Mockito.verify(meetingRepository).delete(mockMeeting);


        Mockito.when(meetingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(MeetingNotExistsException.class, () -> {
            meetingService.deleteMeeting(MOCK_ID + 1L);
        });
    }
}