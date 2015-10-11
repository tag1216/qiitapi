package qiitapi.exception;

public class QiitaException extends RuntimeException {

    private static final long serialVersionUID = 1733375367891893856L;

    public QiitaException() {
    }

    public QiitaException(String message) {
        super(message);
    }

    public QiitaException(Throwable cause) {
        super(cause);
    }

    public QiitaException(String message, Throwable cause) {
        super(message, cause);
    }
}
