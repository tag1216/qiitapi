package qiitapi;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import lombok.extern.slf4j.Slf4j;
import qiitapi.exception.QiitaException;

@Slf4j
public class Request<T> {

    public static final String ENDPOINT = "http://qiita.com/api/v2";

    private String path;
    private Class<T> resultType;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, String> pathVars;
    private RequestFilter<T> filter;

    public static <T> Request<T> to(String path, Class<T> resultType) {
        return new Request<>(path, resultType);
    }

    private Request(String path, Class<T> resultType) {
        this.path = path;
        this.resultType = resultType;
        this.params = new LinkedHashMap<>();
        this.headers = new LinkedHashMap<>();
        this.pathVars = new LinkedHashMap<>();
        this.filter = chain -> chain.proceed();
    }

    public Request<T> createCopy(String newPath) throws QiitaException {
        try {
            URIBuilder uriBuilder = new URIBuilder(newPath);
            List<NameValuePair> queryParams = uriBuilder.getQueryParams();

            Request<T> newRequest = new Request<>(uriBuilder.removeQuery().build().toString(), resultType);
            newRequest.params.putAll(this.params);
            newRequest.headers.putAll(this.headers);
            newRequest.pathVars.putAll(this.pathVars);
            newRequest.filter = this.filter;

            queryParams.forEach(param -> newRequest.param(param.getName(), param.getValue()));

            return newRequest;
        } catch (Exception e) {
            throw new QiitaException(e);
        }
    }

    public Request<T> accessToken(String accessToken) {
        return header("Authorization", "Bearer " + accessToken);
    }

    public Request<T> perPage(int perPage) {
        return param("per_page", perPage);
    }

    public Request<T> page(int page) {
        return param("page", page);
    }

    public Request<T> header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Request<T> param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public Request<T> param(String name, int value) {
        return param(name, String.valueOf(value));
    }

    public Request<T> pathVar(String name, String value) {
        pathVars.put(name, value);
        return this;
    }

    public Request<T> filter(RequestFilter<T> filter) {
        RequestFilter<T> prevFilter = this.filter;
        this.filter = chain -> filter.doFilter(() -> prevFilter.doFilter(chain));
        return this;
    }

    public Response<T> get() throws QiitaException {
        return filter.doFilter(this::doGet);
    }

    private Response<T> doGet() throws QiitaException {
        try {
            HttpGet httpGet = buildHttpGet();
            log.debug(httpGet.toString());
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse res = httpClient.execute(httpGet);) {
                return Response.parse(res, resultType);
            }
        } catch (Exception e) {
            throw new QiitaException(e);
        }
    }

    private HttpGet buildHttpGet() throws URISyntaxException {
        String uri = pathVars.entrySet()
                .stream()
                .reduce(path, (p, e) -> StringUtils.replace(p, e.getKey(), e.getValue()), (x, y) -> x);
        URIBuilder uriBuilder = new URIBuilder(uri);
        params.forEach(uriBuilder::addParameter);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        headers.forEach(httpGet::addHeader);
        return httpGet;
    }
}
