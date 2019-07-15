package com.johnnyfivedev.utilpack;

public interface ResultCallback<T> {

    void onResult(T result);

    default void onError(Throwable printedThrowable) {
        printedThrowable.printStackTrace();
    }
}