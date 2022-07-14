package swm.wbj.asyncrum.global.oauth.exception;

public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("Failed to generate token.");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }

}
