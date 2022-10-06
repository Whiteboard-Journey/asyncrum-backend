package swm.wbj.asyncrum.global.oauth.exception;

/**
 * Provider Mismatch 예외처리
 */
public class OAuthProviderMissMatchException extends RuntimeException {

    public OAuthProviderMissMatchException(String message) {
        super(message);
    }
}
