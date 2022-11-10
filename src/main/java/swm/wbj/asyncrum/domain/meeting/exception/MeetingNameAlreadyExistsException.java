package swm.wbj.asyncrum.domain.meeting.exception;

public class MeetingNameAlreadyExistsException extends RuntimeException{
    public MeetingNameAlreadyExistsException(){
        super("해당 방의 이름은 이미 있습니다. 다른 이름을 설정해주세요");
    }

    public MeetingNameAlreadyExistsException(String message){
        super(message);
    }
}
