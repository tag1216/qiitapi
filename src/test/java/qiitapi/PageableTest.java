package qiitapi;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import qiitapi.entity.User;

@Slf4j
public class PageableTest {

    @Test
    public void 件数取得() throws Exception {
        QiitaClient client = QiitaClient.builder()
                .build();

        int count = client.users().totalCount();
        System.out.println(count);
        assertThat(count, not(0));
    }

    @Test
    public void ページネーション() throws Exception {
        QiitaClient client = QiitaClient.builder()
                .build();

        List<User> users1_6 = client.users()
                .perPage(6)
                .limit(1)
                .stream()
                .collect(toList());
        users1_6.stream().map(User::toString).forEach(log::debug);
        assertThat(users1_6.size(), is(6));

        List<User> users1_2 = client.users()
                .perPage(2)
                .limit(1)
                .stream()
                .collect(toList());
        users1_2.stream().map(User::toString).forEach(log::debug);
        assertThat(users1_2.size(), is(2));
        assertThat(users1_2, is(users1_6.subList(0, 2)));

        List<User> users3_6 = client.users()
                .perPage(2)
                .page(2)
                .limit(2)
                .stream()
                .collect(toList());
        users3_6.stream().map(User::toString).forEach(log::debug);
        assertThat(users3_6.size(), is(4));
        assertThat(users3_6, is(users1_6.subList(2, 6)));
    }
}
