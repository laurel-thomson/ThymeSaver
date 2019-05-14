package com.example.laure.thymesaver.UI.Callbacks;

import java.util.HashMap;

public interface ValueCallback {
    void onSuccess(HashMap values);
    void onError(String err);
}
