package com.yumin.mp3encoder;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable!=null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}
