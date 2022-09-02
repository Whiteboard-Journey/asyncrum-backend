package swm.wbj.asyncrum.global.exception;

public class OperationNotAllowedException extends RuntimeException {

    public OperationNotAllowedException() {
        super("허용되지 않은 작업입니다.");
    }

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
