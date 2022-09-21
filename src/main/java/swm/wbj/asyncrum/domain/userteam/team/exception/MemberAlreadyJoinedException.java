package swm.wbj.asyncrum.domain.userteam.team.exception;

public class MemberAlreadyJoinedException extends RuntimeException {

    public MemberAlreadyJoinedException() {
        super("해당 멤버가 이미 팀에 속해있습니다.");
    }

    public MemberAlreadyJoinedException(String message) {
        super(message);
    }
}
