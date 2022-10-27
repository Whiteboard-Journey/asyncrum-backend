package swm.wbj.asyncrum.domain.team.exception;

public class RoomNameNotExistsException extends RuntimeException{

    public RoomNameNotExistsException() {
        super("해당 이름의 미팅룸이 없습니다.");
    }

    public RoomNameNotExistsException(String message) {
        super(message);
    }
}
