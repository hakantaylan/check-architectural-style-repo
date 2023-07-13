package com.example.archunit.util;

import com.example.archunit.persistence.IPersistenceSession;

public class ResourceCloser {

    public static void close(IPersistenceSession session, AutoCloseable ...closeables) {
        try {
            session.close();
        } catch (Exception ignored) {

        }
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception ignored) {

            }
        }
    }
}
