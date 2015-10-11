package qiitapi;

import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static qiitapi.Request.ENDPOINT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import qiitapi.Request;
import qiitapi.RequestFilter;
import qiitapi.Response;
import qiitapi.entity.Item;
import qiitapi.entity.User;

public class RequestTest {

    TestProperties properties;

    @Before
    public void setup() throws Exception {
        this.properties = new TestProperties();
    }

    @Test
    public void パスパラメータ指定() throws Exception {
        Response<User> response = Request.to(ENDPOINT + "/users/:user_id", User.class)
                .pathVar(":user_id", properties.getValidUserId())
                .get();
        assertThat(response.isError(), is(false));
        assertThat(response.getContent(), notNullValue());
        assertThat(response.getContent().getId(), is(properties.getValidUserId()));
    }

    @Test
    public void アクセストークン正常() throws Exception {
        Response<User> response = Request.to(ENDPOINT + "/users/:user_id", User.class)
                .accessToken(properties.getValidAccessToken())
                .pathVar(":user_id", properties.getValidUserId())
                .get();
        assertThat(response.isError(), is(false));
        assertThat(response.getContent(), notNullValue());
        assertThat(response.getContent().getId(), is(properties.getValidUserId()));
    }

    @Test
    public void アクセストークン不正() throws Exception {
        Response<User> response = Request.to(ENDPOINT + "/users/:user_id", User.class)
                .accessToken(properties.getInvalidAccessToken())
                .pathVar(":user_id", properties.getValidUserId())
                .get();
        assertThat(response.isError(), is(true));
        assertThat(response.getStatusCode(), is(SC_UNAUTHORIZED));
        assertThat(response.getContent(), nullValue());
    }

    @Test
    public void ページネーション指定() throws Exception {
        Request<User[]> request = Request.to(ENDPOINT + "/users", User[].class);
        Response<User[]> response = request.perPage(4).page(1).get();
        assertThat(response.isError(), is(false));
        assertThat(response.getContent(), notNullValue());
        User[] users1 = response.getContent();
        assertThat(users1.length, is(4));

        User[] users2 = request.perPage(2).page(2).get().getContent();
        assertThat(users1[2], is(users2[0]));
        assertThat(users1[3], is(users2[1]));
    }

    @Test
    public void クエリーパラメータ指定() throws Exception {
        Response<Item[]> response = Request.to(ENDPOINT + "/items", Item[].class)
                .param("query", "tag:java")
                .perPage(1)
                .page(1)
                .get();
        assertThat(response.isError(), is(false));
        assertThat(response.getContent(), notNullValue());
        List<String> tags = response.getContent()[0].getTags().stream()
                .map(Item.Tag::getName)
                .collect(toList());
        assertThat(tags, hasItem("Java"));
    }


    @Test
    public void フィルター() throws Exception {
        List<String> filterCalledHistory = new ArrayList<>();
        RequestFilter<User[]> filter1 = invocation -> {
            filterCalledHistory.add("a");
            return invocation.proceed();
        };
        RequestFilter<User[]> filter2 = invocation -> {
            filterCalledHistory.add("b");
            return invocation.proceed();
        };

        @SuppressWarnings("unused")
        Response<User[]> response = Request.to(ENDPOINT + "/users", User[].class)
                .filter(filter1)
                .filter(filter2)
                .get();
        assertThat(filterCalledHistory, is(Arrays.asList("b", "a")));
    }
}
