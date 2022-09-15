package swm.wbj.asyncrum.domain.whiteboard.exception;

public class WhiteboardNotExistsException extends RuntimeException {

    public WhiteboardNotExistsException() {
        super("해당 화이트보드 문서가 존재하지 않습니다.");
    }

    public WhiteboardNotExistsException(String message) {
        super(message);
    }
}

