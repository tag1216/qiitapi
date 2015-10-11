package qiitapi;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static qiitapi.Request.ENDPOINT;
import static qiitapi.RequestFilters.retry;
import static qiitapi.RequestFilters.throwException;
import static qiitapi.RequestFilters.waitBeforeRequest;
import static qiitapi.RequestFilters.waitRateReset;

import lombok.Builder;
import qiitapi.entity.Item;
import qiitapi.entity.Tag;
import qiitapi.entity.User;
import qiitapi.exception.QiitaException;
import qiitapi.exception.QiitaNotFoundException;
import qiitapi.exception.QiitaUnauthorizedException;

@Builder(builderClassName = "Builder")
public class QiitaClient {

    public static class Builder {
        public Builder() {
            this.defaultPerPage = 100;
            this.defaultPageLimit = 100;
            this.retryLimit = 10;
            this.requestWait = 200;
        }
    }

    private String accessToken;
    private int defaultPerPage;
    private int defaultPageLimit;
    private int retryLimit;
    private int requestWait;

    public Pageable<User> users() {
        return pageable(request("/users", User[].class));
    }

    public User user(String userId) throws QiitaException {
        return singleResult(request("/users/:user_id", User.class).pathVar(":user_id", userId));
    }

    public Pageable<Item> userItems(String userId) {
        return pageable(request("/users/:user_id/items", Item[].class).pathVar(":user_id",  userId));
    }

    public Pageable<Item> userStocks(String userId) {
        return pageable(request("/users/:user_id/stocks", Item[].class).pathVar(":user_id", userId));
    }

    public Pageable<Tag> userFollowingTags(String userId) {
        return pageable(request("/users/:user_id/following_tags", Tag[].class).pathVar(":user_id", userId));
    }

    public Pageable<Tag> tags() {
        return pageable(request("/tags", Tag[].class));
    }

    public Tag tag(String tagId) {
        return singleResult(request("/tags/:tag_id", Tag.class).pathVar(":tag_id", tagId));
    }

    public Pageable<Item> tagItems(String tagId) {
        return pageable(request("/tags/:tag_id/items", Item[].class).pathVar(":tag_id", tagId));
    }

    public Pageable<Item> items() {
        return pageable(request("/items", Item[].class));
    }

    public Pageable<Item> items(String query) {
        return pageable(request("/items", Item[].class).param("query", query));
    }

    public Item item(String itemId) {
        return singleResult(request("/items/:item_id", Item.class).pathVar(":item_id", itemId));
    }

    public Pageable<User> itemStockers(String itemId) {
        return pageable(request("/items/:item_id/stockers", User[].class).pathVar(":item_id", itemId));
    }

    private <T> Request<T> request(String path, Class<T> resultType) {
        Request<T> request = Request.to(ENDPOINT + path, resultType)
                .filter(waitBeforeRequest(requestWait))
                .filter(waitRateReset())
                .filter(retry(retryLimit))
                .filter(throwException(SC_NOT_FOUND, statusCode -> new QiitaNotFoundException()))
                .filter(throwException(SC_UNAUTHORIZED, statusCode -> new QiitaUnauthorizedException()));
        if (accessToken != null) {
            request.accessToken(accessToken);
        }
        return request;
    }

    private <T> Pageable<T> pageable(Request<T[]> request) {
        return new Pageable<T>(request.page(1).perPage(defaultPerPage), defaultPerPage, 1, defaultPageLimit);
    }

    private <T> T singleResult(Request<T> request) {
        Response<T> response = request.get();
        return response.getContent();
    }
}
