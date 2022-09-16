package swm.wbj.asyncrum.domain.userteam.team.exception;

public class CodeAlreadyInUseException extends RuntimeException {

    public CodeAlreadyInUseException() {
        super("해당 코드는 이미 사용중입니다.");
    }

    public CodeAlreadyInUseException(String message) {
        super(message);
    }
}
