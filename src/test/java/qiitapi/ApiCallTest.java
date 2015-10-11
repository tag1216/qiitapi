package qiitapi;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import qiitapi.entity.Item;
import qiitapi.entity.Tag;
import qiitapi.entity.User;

@Slf4j
public class ApiCallTest {

    TestProperties properties;
    QiitaClient client;

    @Before
    public void setup() throws Exception {
        this.properties = new TestProperties();
        this.client = QiitaClient.builder()
                .defaultPerPage(2)
                .defaultPageLimit(1)
                .build();
    }

    @Test
    public void testUsers() throws Exception {
        List<User> users = client.users()
                .stream()
                .collect(toList());

        users.stream().map(User::toString).forEach(log::debug);

        assertThat(users.size(), is(2));
    }

    @Test
    public void testUser() throws Exception {
        User user = client.user(properties.getValidUserId());

        log.debug(user.toString());

        assertThat(user.getId(), is(properties.getValidUserId()));
    }

    @Test
    public void testUserItems() throws Exception {
        List<Item> items = client.userItems(properties.getValidUserId())
                .stream()
                .collect(toList());

        items.stream().map(Item::toString).forEach(log::debug);

        assertThat(items.size(), is(2));
    }

    @Test
    public void testUserStocks() throws Exception {
        List<Item> items = client.userStocks(properties.getValidUserId())
                .stream()
                .collect(toList());

        items.stream().map(Item::toString).forEach(log::debug);

        assertThat(items.size(), is(2));
    }

    @Test
    public void testUserFollowingTags() throws Exception {
        List<Tag> tags = client.userFollowingTags(properties.getValidUserId())
                .stream()
                .collect(toList());

        tags.stream().map(Tag::toString).forEach(log::debug);

        assertThat(tags.size(), is(2));
    }

    @Test
    public void testTags() throws Exception {
        List<Tag> tags = client.tags()
                .stream()
                .collect(toList());

        tags.stream().map(Tag::toString).forEach(log::debug);

        assertThat(tags.size(), is(2));
    }

    @Test
    public void testTag() throws Exception {
        Tag tag = client.tag("Java");

        assertThat(tag.getId(), is("Java"));
    }

    @Test
    public void testTagItems() throws Exception {
        List<Item> items = client.tagItems("java")
                .stream()
                .collect(toList());

        items.stream().map(Item::toString).forEach(log::debug);

        assertThat(items.size(), is(2));
    }

    @Test
    public void testItems() throws Exception {
        List<Item> items = client.items()
                .stream()
                .collect(toList());

        items.stream().map(Item::toString).forEach(log::debug);

        assertThat(items.size(), is(2));
    }

    @Test
    public void testItemsQuery() throws Exception {
        List<Item> items = client.items("user:" + properties.getValidUserId())
                .stream()
                .collect(toList());

        items.stream().map(Item::toString).forEach(log::debug);

        assertThat(items.size(), is(2));
        assertThat(items.stream().map(item -> item.getUser().getId()).distinct().collect(toList()),
                contains(properties.getValidUserId()));
    }

    @Test
    public void testItem() throws Exception {
        Item item = client.item(properties.getValidItemId());

        log.debug(item.toString());

        assertThat(item.getId(), is(properties.getValidItemId()));
        assertThat(item.getUser().getId(), is(properties.getValidUserId()));
    }

    @Test
    public void testItemStockers() throws Exception {
        List<User> users = client.itemStockers(properties.getValidItemId())
                .stream()
                .collect(toList());

        users.stream().map(User::toString).forEach(log::debug);

        assertThat(users.size(), is(2));
    }
}
