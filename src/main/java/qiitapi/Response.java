package qiitapi;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import qiitapi.entity.QiitaError;
import qiitapi.exception.QiitaException;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Response<T> {

    private static final Pattern HEADER_LINK_FIRST = Pattern.compile("^<(.*?)>; rel=first$");
    private static final Pattern HEADER_LINK_PREV = Pattern.compile("^<(.*?)>; rel=prev$");
    private static final Pattern HEADER_LINK_NEXT = Pattern.compile("^<(.*?)>; rel=next$");
    private static final Pattern HEADER_LINK_LAST = Pattern.compile("^<(.*?)>; rel=last$");

    private int statusCode;

    private int totalCount;
    private int rateRemaining;
    private Date rateReset;
    private String first;
    private String prev;
    private String next;
    private String last;

    private T content;
    private QiitaError error;

    public static <T> Response<T> parse(HttpResponse res, Class<T> resultType) throws QiitaException {

        try {
            HttpEntity entity = res.getEntity();

            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode != SC_OK) {
                log.debug("statusCode:" + statusCode);
                if (entity.getContentType().getValue().equals("application/json")) {
                    return Response.<T>builder()
                            .statusCode(statusCode)
                            .error(getContent(QiitaError.class, entity))
                            .build();
                } else {
                    return Response.<T>builder()
                            .statusCode(statusCode)
                            .build();
                }
            }

            ResponseBuilder<T> builder = Response.<T>builder()
                    .statusCode(statusCode)
                    .content(getContent(resultType, entity))
                    .totalCount(getIntHeader(res, "Total-Count"))
                    .rateRemaining(getIntHeader(res, "Rate-Remaining"))
                    .rateReset(new Date(getLongHeader(res, "Rate-Reset") * 1000));

            Header link = res.getFirstHeader("Link");
            if (link != null) {
                List<HeaderElement> links = Arrays.asList(link.getElements());
                builder.first(getLink(links, HEADER_LINK_FIRST))
                        .prev(getLink(links, HEADER_LINK_PREV))
                        .next(getLink(links, HEADER_LINK_NEXT))
                        .last(getLink(links, HEADER_LINK_LAST));
            }

            log.debug("Rate Remaining:" + builder.rateRemaining);

            return builder.build();
        } catch (Exception e) {
            throw new QiitaException(e);
        }
    }

    public boolean isError() {
        return statusCode != SC_OK;
    }

    public boolean isRateLimitExceeded() {
        return statusCode == SC_FORBIDDEN && Objects.equals(error.getType(), "rate_limit_exceeded");
    }

    private static <R> R getContent(Class<R> resultType, HttpEntity entity) throws IOException {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();
        R result = gson.fromJson(EntityUtils.toString(entity), resultType);
        return result;
    }

    private static int getIntHeader(HttpResponse res, String name) {
        return Optional.ofNullable(res.getFirstHeader(name))
                .map(Header::getValue)
                .map(Integer::valueOf)
                .orElse(0);
    }

    private static long getLongHeader(HttpResponse res, String name) {
        return Optional.ofNullable(res.getFirstHeader(name))
                .map(Header::getValue)
                .map(Long::valueOf)
                .orElse(0L);
    }

    private static String getLink(List<HeaderElement> links, Pattern pattern) {
        return links.stream()
                .map(HeaderElement::toString)
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(m -> m.group(1))
                .findFirst()
                .orElse(null);
    }
}
