package swm.wbj.asyncrum.domain.record.record.exception;

public class RecordNotExistsException extends RuntimeException {

    public RecordNotExistsException() {
        super("해당 녹화가 존재하지 않습니다.");
    }

    public RecordNotExistsException(String message) {
        super(message);
    }
}
