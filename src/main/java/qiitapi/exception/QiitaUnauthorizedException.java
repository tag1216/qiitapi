package qiitapi.exception;

public class QiitaUnauthorizedException extends QiitaException {

    private static final long serialVersionUID = 6129172091360240900L;

    public QiitaUnauthorizedException() {
    }

    public QiitaUnauthorizedException(String message) {
        super(message);
    }

    public QiitaUnauthorizedException(Throwable cause) {
        super(cause);
    }

    public QiitaUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
