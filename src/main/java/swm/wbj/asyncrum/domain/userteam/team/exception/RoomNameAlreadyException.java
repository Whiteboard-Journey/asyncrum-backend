package swm.wbj.asyncrum.domain.userteam.team.exception;

public class RoomNameAlreadyException extends RuntimeException{

    public RoomNameAlreadyException() {
        super("해당 이름의 미팅룸이 이미 생성되었습니다. 다른 이름으로 생성해주세요");
    }

    public RoomNameAlreadyException(String message) {
        super(message);
    }
}
