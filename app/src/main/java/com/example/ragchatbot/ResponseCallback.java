package com.example.ragchatbot;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
