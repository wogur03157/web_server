package com.aroasoft.core.tcpserver.tcpip;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class AROAEventHandler {

    private static final int MAX_THREAD_POOL = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(AROAEventHandler.class);

    /**
     * Note : ArrayList may occur ConcurrentModificationException so using
     * CopyOnWriteArrayList for prevent Exception based on multi thread. Do not
     * use below source code. private static List<EventListener> listeners = new
     * ArrayList<EventListener>();
     */
    private static List<AROAEventListener> aroaEventListeners = new CopyOnWriteArrayList<AROAEventListener>();

    private static synchronized List<AROAEventListener> getListeners() {
        return aroaEventListeners;
    }

    public static synchronized void addListener(AROAEventListener aroaEventListener) {
        if (getListeners().indexOf(aroaEventListener) == -1) {
            aroaEventListeners.add(aroaEventListener);
        }
    }

    public static synchronized void removeListener(AROAEventListener aroaEventListener) {
        if (getListeners().indexOf(aroaEventListener) != -1) {
            aroaEventListeners.remove(aroaEventListener);
        }
    }

    public static synchronized void callEvent(final Class<?> caller, ByteBuf event) {
        callEvent(caller, event, true);
    }

    public static synchronized void callEvent(final Class<?> caller, ByteBuf event, boolean doAsynch) {
        if (doAsynch) {
            callEventByAsynch(caller, event);
        } else {
            callEventBySynch(caller, event);
        }
    }

    private static synchronized void callEventByAsynch(final Class<?> caller, final ByteBuf event) {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL);

        LOGGER.debug("[Event occur : <{}> by <{}>]", new Object[] { event, caller.getName() });

        for (final AROAEventListener listener : aroaEventListeners) {
            executorService.execute(new Runnable() {
                public void run() {
                    if (listener.getClass().getName().equals(caller.getName())) {
                        LOGGER.debug("[Event skip : <{}> by self <{}>]", new Object[] { event, caller.getName() });
                    } else {
                        LOGGER.debug("[Event catch : <{}> by <{}>]", new Object[] { event, listener.getClass().getName() });

                        listener.onEvent(event);
                    }
                }
            });
        }

        executorService.shutdown();
    }

    private static synchronized void callEventBySynch(final Class<?> caller, final ByteBuf event) {
        LOGGER.debug("[Event occur : <{}> by <{}>]", new Object[] { event, caller.getName() });

        for (final AROAEventListener listener : aroaEventListeners) {
            if (listener.getClass().getName().equals(caller.getName())) {
                LOGGER.debug("[Event skip : <{}> by self <{}>]", new Object[] { event, caller.getName() });
            } else {
                LOGGER.debug("[Event catch : <{}> by <{}>]", new Object[] { event, listener.getClass().getName() });

                listener.onEvent(event);
            }
        }
    }
}
