package com.boredream.baseapplication.view.loading;

public interface ILoadingView {

    void doOnSubscribe(boolean loadMore);

    void doOnError(Throwable throwable, boolean loadMore);

    <T> void doOnNext(T t, boolean loadMore);

}
