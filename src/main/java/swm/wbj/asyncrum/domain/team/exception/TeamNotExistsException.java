package swm.wbj.asyncrum.domain.team.exception;

public class TeamNotExistsException extends RuntimeException {

    public TeamNotExistsException() {
        super("해당 팀에 속해있지 않습니다.");
    }

    public TeamNotExistsException(String message) {
        super(message);
    }
}
