package qiitapi;

import qiitapi.exception.QiitaException;

@FunctionalInterface
public interface RequestFilter<T> {
    public Response<T> doFilter(RequestInvocation<T> invocation) throws QiitaException;
}
