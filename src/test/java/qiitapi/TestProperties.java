package qiitapi;

import java.util.Properties;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class TestProperties {

    String validUserId;
    String validAccessToken;
    String validItemId;
    String invalidUserId;
    String invalidAccessToken;
    String invalidItemId;

    public TestProperties() throws Exception {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/test.properties"));
        this.validUserId        = properties.getProperty("qiitapi.test.valid.user.id");
        this.validAccessToken   = properties.getProperty("qiitapi.test.valid.accessToken");
        this.validItemId        = properties.getProperty("qiitapi.test.valid.item.id");
        this.invalidUserId      = properties.getProperty("qiitapi.test.invalid.user.id");
        this.invalidAccessToken = properties.getProperty("qiitapi.test.invalid.accessToken");
        this.invalidItemId      = properties.getProperty("qiitapi.test.invalid.item.id");
        log.debug(this.toString());
    }
}
