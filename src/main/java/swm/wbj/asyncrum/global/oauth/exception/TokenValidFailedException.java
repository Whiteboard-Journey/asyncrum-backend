package swm.wbj.asyncrum.global.oauth.exception;

/**
 * 토큰 Validation 예외처리
 */
public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("토큰 검증 실패.");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }
}
