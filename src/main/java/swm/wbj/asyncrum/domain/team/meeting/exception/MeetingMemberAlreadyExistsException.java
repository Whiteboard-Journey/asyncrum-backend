package swm.wbj.asyncrum.domain.team.meeting.exception;

public class MeetingMemberAlreadyExistsException extends RuntimeException{
    public MeetingMemberAlreadyExistsException(){
        super("해당 멤버는 이미 초대되었습니다");
    }

    public MeetingMemberAlreadyExistsException(String message){
        super(message);
    }
}
