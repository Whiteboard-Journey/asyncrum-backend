package swm.wbj.asyncrum.domain.team.meeting.exception;

public class MeetingNotExistsException extends RuntimeException {

    public MeetingNotExistsException(){
        super("해당 미팅이 존재하지 않습니다");
    }

    public MeetingNotExistsException(String message){
        super(message);
    }
}
