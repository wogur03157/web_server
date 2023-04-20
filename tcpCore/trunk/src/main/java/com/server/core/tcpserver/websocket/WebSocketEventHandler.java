package com.aroasoft.core.tcpserver.websocket;


import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class WebSocketEventHandler {

    private static final int MAX_THREAD_POOL = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventHandler.class);

    /**
     * Note : ArrayList may occur ConcurrentModificationException so using
     * CopyOnWriteArrayList for prevent Exception based on multi thread. Do not
     * use below source code. private static List<EventListener> listeners = new
     * ArrayList<EventListener>();
     */
    private static List<WebSocketEventListener> WebSocketEventListeners = new CopyOnWriteArrayList<WebSocketEventListener>();

    private static synchronized List<WebSocketEventListener> getListeners() {
        return WebSocketEventListeners;
    }

    public static synchronized void addListener(WebSocketEventListener webSocketEventListener) {
        if (getListeners().indexOf(webSocketEventListener) == -1) {
            WebSocketEventListeners.add(webSocketEventListener);
        }
    }

    public static synchronized void removeListener(WebSocketEventListener webSocketEventListener) {
        if (getListeners().indexOf(webSocketEventListener) != -1) {
            WebSocketEventListeners.remove(webSocketEventListener);
        }
    }


}
