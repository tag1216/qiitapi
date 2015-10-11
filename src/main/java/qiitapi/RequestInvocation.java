package qiitapi;

import qiitapi.exception.QiitaException;

@FunctionalInterface
public interface RequestInvocation<T> {
    public Response<T> proceed() throws QiitaException;
}
