package org.ovirt.engine.core.bll.utils;

import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.ovirt.engine.core.utils.EngineLocalConfig;

public class ThreadPoolInfo {

    private String poolName;
    private Map<Thread.State, AtomicInteger> threadStateMap = new TreeMap<>();
    private int maxThreads;
    private static AtomicInteger ZERO = new AtomicInteger(0);
    private static Map<String, String> configMaxThreadAttrNamesMap = new HashMap<>();
    private static String msg = "Thread pool '%s' is using %s threads out of %s and %s tasks are waiting in the queue.";
    static {
        configMaxThreadAttrNamesMap.put("commandCoordinator", "COMMAND_COORDINATOR_THREAD_POOL_SIZE");
        configMaxThreadAttrNamesMap.put("engine", "ENGINE_THREAD_POOL_SIZE");
        configMaxThreadAttrNamesMap.put("engineScheduled", "ENGINE_SCHEDULED_THREAD_POOL_SIZE");
        configMaxThreadAttrNamesMap.put("hostUpdatesChecker", "HOST_CHECK_FOR_UPDATES_THREAD_POOL_SIZE");
    }

    public ThreadPoolInfo(String poolName) {
        this.poolName = poolName;
        maxThreads = EngineLocalConfig.getInstance().getInteger(configMaxThreadAttrNamesMap.get(poolName) , 1);
    }

    public void processThreadInfo(ThreadInfo threadInfo) {
        Thread.State state = threadInfo.getThreadState();
        threadStateMap.putIfAbsent(state, new AtomicInteger(0));
        threadStateMap.get(state).incrementAndGet();
    }

    @Override
    public String toString() {
        int usedThreads = threadStateMap.getOrDefault(Thread.State.NEW, ZERO).get() +
                threadStateMap.getOrDefault(Thread.State.RUNNABLE, ZERO).get() +
                threadStateMap.getOrDefault(Thread.State.BLOCKED, ZERO).get();
        int waitingThreads = threadStateMap.getOrDefault(Thread.State.TIMED_WAITING, ZERO).get() +
                threadStateMap.getOrDefault(Thread.State.WAITING, ZERO).get();
        return String.format(msg, poolName, usedThreads, maxThreads, waitingThreads);
    }
}
