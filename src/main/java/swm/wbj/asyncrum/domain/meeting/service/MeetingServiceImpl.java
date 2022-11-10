package swm.wbj.asyncrum.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.meeting.dto.*;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.meeting.repository.MeetingRepository;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.domain.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.meeting.exception.MeetingNotExistsException;
import swm.wbj.asyncrum.domain.team.service.TeamService;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.global.media.AwsService;
import swm.wbj.asyncrum.global.type.FileType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static swm.wbj.asyncrum.global.media.AwsService.MEETING_BUCKET_NAME;
import static swm.wbj.asyncrum.global.media.AwsService.MEETING_FILE_PREFIX;


@RequiredArgsConstructor
@Transactional
@Service
public class MeetingServiceImpl implements MeetingService{

    private final MeetingRepository meetingRepository;
    private final TeamService teamService;

    private final MemberRepository memberRepository;
    private final AwsService awsService;


    @Override
    public MeetingCreateResponseDto createMeeting(MeetingCreateRequestDto requestDto) {
        Team currentTeam = teamService.getCurrentTeam(requestDto.getTeamId());
        Meeting meeting = requestDto.toEntity(currentTeam);
        Meeting savedMeeting = meetingRepository.save(meeting);
        Long meetingId = meetingRepository.save(meeting).getId();

        String meetingFileKey = createMeetingFileKey(currentTeam.getId(), meetingId);
        String preSignedURL = awsService.generatePresignedURL(meetingFileKey, MEETING_BUCKET_NAME, FileType.MP4);
        meeting.updateMeetingFileMetadata(meetingFileKey, awsService.getObjectURL(meetingFileKey, MEETING_BUCKET_NAME));

        return new MeetingCreateResponseDto(savedMeeting.getId(), preSignedURL);
    }

    private String createMeetingFileKey(Long teamId, Long meetingId) {
        return MEETING_FILE_PREFIX  + "_" + teamId + "_" + meetingId + "." + FileType.MP4.getName();
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingReadResponseDto readMeeting(Long id) {

        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingNotExistsException::new);
        return new MeetingReadResponseDto(meeting);
    }


    @Override
    @Transactional(readOnly = true)
    public MeetingReadAllResponseDto readAllMeeting(Long teamId) {
        Team team = teamService.getCurrentTeam(teamId);
        List<Meeting> meetings = meetingRepository.findAllByTeam(team);
        return new MeetingReadAllResponseDto(meetings);
    }

    @Override
    public MeetingUpdateResponseDto updateMeeting(Long id, MeetingUpdateRequestDto requestDto) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingNotExistsException::new);

        meeting.updateMeetingStatus(
                requestDto.getStatus()

        );

        return new MeetingUpdateResponseDto(meeting.getId());
    }

    @Override
    public void deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingNotExistsException::new);
        meetingRepository.delete(meeting);

    }

    @Override
    public MeetingUpdateResponseDto addMeetingMember(Long id, TeamMeetingRequestDto requestDto) {
        Meeting meeting = getMeeting(id).orElseThrow(MeetingNotExistsException::new);
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(MemberNotExistsException::new);
        Set<String> participants = Optional.of(meeting.getParticipants()).orElse(new HashSet<>());
        participants.add(member.getFullname());
        meeting.updateParticipants(participants);
        return new MeetingUpdateResponseDto(meetingRepository.save(meeting).getId());
    }

    @Override
    public void removeMeetingMember(Long id, Long memberId) {
        Meeting meeting = getMeeting(id).orElseThrow(MeetingNotExistsException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotExistsException::new);
        Set<String> participants = Optional.of(meeting.getParticipants()).orElse(new HashSet<>());
        participants.remove(member.getFullname());
        meeting.updateParticipants(participants);

    }

    private Optional<Meeting> getMeeting(Long id) {
        return meetingRepository.findById(id);
    }
}
