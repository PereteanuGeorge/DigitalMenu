package com.example.george.digitalmenu;

import android.support.v4.util.Consumer;

interface QrCodeScanner {
    void scan(Consumer<String> callback);
}
