package qiitapi;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import qiitapi.entity.User;
import qiitapi.exception.QiitaUnauthorizedException;

public class AccessTokenTest {

    TestProperties properties;

    @Before
    public void setup() throws Exception {
        this.properties = new TestProperties();
    }

    @Test
    public void アクセストークン正常() throws Exception {
        QiitaClient client = QiitaClient.builder()
                .accessToken(properties.getValidAccessToken())
                .build();
        User user = client.user(properties.getValidUserId());
        assertThat(user, notNullValue());
    }

    @Test(expected = QiitaUnauthorizedException.class)
    public void アクセストークン不正() throws Exception {
        QiitaClient client = QiitaClient.builder()
                .accessToken(properties.getInvalidAccessToken())
                .build();
        client.user(properties.getValidUserId());
    }
}
