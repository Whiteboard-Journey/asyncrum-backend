package swm.wbj.asyncrum.domain.record.comment.exception;

public class CommentNotExistsException extends RuntimeException{

    public CommentNotExistsException() {
        super("해당 영상 댓글이 존재 하지 않습니다.");
    }

    public CommentNotExistsException(String message) {
        super(message);
    }
}
