package swm.wbj.asyncrum.domain.meeting.exception;

public class MeetingMemberAlreadyInUseException extends RuntimeException{

    public MeetingMemberAlreadyInUseException() {
        super("해당 멤버는 이미 초대했습니다.");
    }

    public MeetingMemberAlreadyInUseException(String message) {
        super(message);
    }
}
