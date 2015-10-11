package qiitapi.exception;

public class QiitaNotFoundException extends QiitaException {

    private static final long serialVersionUID = -3541096144393597993L;

    public QiitaNotFoundException() {
    }

    public QiitaNotFoundException(String message) {
        super(message);
    }

    public QiitaNotFoundException(Throwable cause) {
        super(cause);
    }

    public QiitaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
