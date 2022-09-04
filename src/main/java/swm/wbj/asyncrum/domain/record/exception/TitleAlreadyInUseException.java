package swm.wbj.asyncrum.domain.record.exception;

public class TitleAlreadyInUseException extends RuntimeException {

    public TitleAlreadyInUseException() {
        super("해당 녹화가 존재하지 않습니다.");
    }

    public TitleAlreadyInUseException(String message) {
        super(message);
    }
}
