package swm.wbj.asyncrum.domain.userteam.team.exception;

public class MemberNotInTeamException extends RuntimeException {

    public MemberNotInTeamException() {
        super("해당 팀이 존재하지 않습니다.");
    }

    public MemberNotInTeamException(String message) {
        super(message);
    }
}
