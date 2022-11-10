package swm.wbj.asyncrum.domain.meeting.service;

import swm.wbj.asyncrum.domain.meeting.dto.*;

public interface MeetingService {

    MeetingCreateResponseDto createMeeting(MeetingCreateRequestDto requestDto);

    MeetingReadResponseDto readMeeting(Long id);

    MeetingReadAllResponseDto readAllMeeting(Long teamId);

    MeetingUpdateResponseDto updateMeeting(Long id, MeetingUpdateRequestDto requestDto);

    void deleteMeeting(Long id);

    MeetingUpdateResponseDto addMeetingMember(Long id, TeamMeetingRequestDto requestDto);

    void removeMeetingMember(Long id, Long memberId);
}
