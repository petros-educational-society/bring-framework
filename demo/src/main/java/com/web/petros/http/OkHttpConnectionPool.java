package com.web.petros.http;

import okhttp3.ConnectionPool;

import java.util.concurrent.TimeUnit;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public final class OkHttpConnectionPool {
    private static final int MAX_IDLE_CONNECTIONS = 100;
    private static final long KEEP_ALIVE_DURATION_SEC = 120;

    public static final ConnectionPool CONNECTION_POOL =
            new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION_SEC, TimeUnit.SECONDS);
}
