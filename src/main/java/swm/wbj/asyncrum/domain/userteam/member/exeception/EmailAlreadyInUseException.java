package swm.wbj.asyncrum.domain.userteam.member.exeception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("해당 이메일은 이미 사용중입니다.");
    }

    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
