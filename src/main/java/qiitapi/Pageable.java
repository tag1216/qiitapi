package qiitapi;

import static java.util.Spliterator.SORTED;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import qiitapi.exception.QiitaException;

@AllArgsConstructor
@Slf4j
public class Pageable<T> {

    private Request<T[]> request;
    private int perPage;
    private int page;
    private int limit;

    Pageable(Request<T[]> request) {
        this.request = request;
    }

    public Pageable<T> perPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public Pageable<T> page(int page) {
        this.page = page;
        return this;
    }

    public Pageable<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public int totalCount() throws QiitaException {
        return request.perPage(1).page(1).get().getTotalCount();
    }

    public Stream<T> stream() throws QiitaException {
        return StreamSupport.stream(spliteratorUnknownSize(responseIterator(), SORTED), false)
                .flatMap(response -> {
                    T[] contents = response.getContent();
                    return contents == null ? Stream.empty() : Stream.of(contents);
                });
    }

    private Iterator<Response<T[]>> responseIterator() throws QiitaException {
        Response<T[]> response = firstResponse();
        return new Iterator<Response<T[]>>() {
            int count = 0;
            Response<T[]> firstResponse = response;
            Response<T[]> nextResponse = null;
            @Override
            public boolean hasNext() {
                return firstResponse != null ||
                        (count < 100 && count < limit && nextResponse != null && nextResponse.getNext() != null);
            }

            @Override
            public Response<T[]> next() {
                if (!hasNext()) throw new IllegalStateException();
                count++;
                if (firstResponse != null) {
                    nextResponse = firstResponse;
                    firstResponse = null;
                    return nextResponse;
                }
                nextResponse = nextResponse(nextResponse);
                return nextResponse;
            }
        };
    }

    private Response<T[]> firstResponse() {
        return request.perPage(perPage).page(page).get();
    }

    private Response<T[]> nextResponse(Response<T[]> response) {
        if (response == null) {
            log.info("responseがnull");
            return null;
        }
        if (response.getNext() == null) {
            log.info("nextがnull");
            return null;
        }
        return request.createCopy(response.getNext()).get();
    }
}
