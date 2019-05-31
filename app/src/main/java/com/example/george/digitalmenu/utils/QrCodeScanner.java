package com.example.george.digitalmenu.utils;

import android.support.v4.util.Consumer;

public interface QrCodeScanner {
    void scan(Consumer<String> callback);
}
