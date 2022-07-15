package swm.wbj.asyncrum.global.oauth.exception;

/**
 * 토큰 Validation 예외처리
 */
public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("Failed to generate token.");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }

}
