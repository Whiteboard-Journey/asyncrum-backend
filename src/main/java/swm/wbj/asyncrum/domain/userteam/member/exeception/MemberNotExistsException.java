package swm.wbj.asyncrum.domain.userteam.member.exeception;

/**
 * 멤버 조회 예외처리
 */
public class MemberNotExistsException extends RuntimeException {

    public MemberNotExistsException() {
        super("해당 멤버가 존재하지 않습니다.");
    }

    public MemberNotExistsException(String message) {
        super(message);
    }
}
