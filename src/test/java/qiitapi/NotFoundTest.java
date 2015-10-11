package qiitapi;

import static java.util.stream.Collectors.toList;

import org.junit.Before;
import org.junit.Test;

import qiitapi.exception.QiitaNotFoundException;

public class NotFoundTest {

    TestProperties properties;
    QiitaClient client;

    @Before
    public void setup() throws Exception {
        this.properties = new TestProperties();
        this.client = QiitaClient.builder().build();
    }

    @Test(expected = QiitaNotFoundException.class)
    public void 単一結果取得_NotFound() throws Exception {
        client.item(properties.getInvalidItemId());
    }

    @Test(expected = QiitaNotFoundException.class)
    public void 件数取得_NotFound() throws Exception {
        client.userItems(properties.getInvalidUserId()).totalCount();
    }

    @Test(expected = QiitaNotFoundException.class)
    public void 複数結果取得_NotFound() throws Exception {
        client.userItems(properties.getInvalidUserId()).stream().collect(toList());
    }
}
