package swm.wbj.asyncrum.domain.record.bookmark.exception;

public class BookmarkNotExistsException extends RuntimeException {

    public BookmarkNotExistsException() {
        super("해당 북마크가 존재하지 않습니다.");
    }

    public BookmarkNotExistsException(String message) {
        super(message);
    }
}
