package qiitapi;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import qiitapi.exception.QiitaException;

@Slf4j
public class RequestFilters {

    private RequestFilters() {}

    /**
     * リクエスト前に指定時間待機する。
     *
     * @param ms 待機時間(ミリ秒)
     * @return
     */
    public static <T> RequestFilter<T> waitBeforeRequest(long ms) {
        return invocation -> {
            try {
                log.debug("" + ms + "秒待機します。");
                Thread.sleep(ms);
            } catch (InterruptedException e) {
            }
            return invocation.proceed();
        };
    }

    /**
     * リクエスト制限を超えた時にリセットされるまで待機する。
     *
     * @return
     */
    public static <T> RequestFilter<T> waitRateReset() {
       return invocation -> {
            Response<T> response = invocation.proceed();
            if (response.isRateLimitExceeded()) {
                log.warn("リクエスト制限を超えました。リセットされるまで待機します。");
                log.warn("リクエスト可能数:" + response.getRateRemaining());
                log.warn("リセット時刻:" + response.getRateReset());
                try {
                    Thread.sleep(response.getRateReset().getTime() - System.currentTimeMillis());
                } catch (InterruptedException e) {
                }
                response = invocation.proceed();
            }
            return response;
        };
    }

    /**
     * レスポンスが指定ステータスコードの場合に例外をスローする。
     *
     * @param statusCode
     * @Param exceptionGenerator
     * @return
     */
    public static <T> RequestFilter<T> throwException(int statusCode, Function<Integer, QiitaException> exceptionGenerator) {
        return invocation -> {
            Response<T> response = invocation.proceed();
            if (response.getStatusCode() == statusCode) throw exceptionGenerator.apply(response.getStatusCode());
            return response;
        };
    }

    /**
     * リクエストエラー発生時に指定回数までリトライを繰り返す。<br>
     * 以下のステータスコードはリトライ対象外とする。
     * <ul>
     * <li>200:OK</li>
     * <li>401:Unauthorized</li>
     * <li>403:Forbidden</li>
     * <li>404:Not Found</li>
     * </ul>
     *
     * @param limit
     * @return
     */
    public static <T> RequestFilter<T> retry(int limit) {
        if (limit < 0) throw new IllegalArgumentException();
        return invocation -> {
            Response<T> response = null;
            int requestCount = 0;
            do {
                response = invocation.proceed();
                requestCount++;
            } while (!unretryableStatusCode.contains(response.getStatusCode()) && requestCount <= limit);
            return response;
        };
    }

    private static List<Integer> unretryableStatusCode = Arrays.asList(SC_OK, SC_UNAUTHORIZED, SC_FORBIDDEN, SC_NOT_FOUND);
}
