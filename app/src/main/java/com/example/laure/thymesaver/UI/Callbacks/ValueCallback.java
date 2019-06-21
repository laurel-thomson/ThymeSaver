package com.example.laure.thymesaver.UI.Callbacks;

public interface ValueCallback<T> {
    void onSuccess(T value);
    void onError(String error);
}
