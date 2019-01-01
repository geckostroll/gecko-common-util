package com.geckostroll.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认线程工厂
 *
 * @author yanhuai
 * @version $Id: DaemonThreadFactory.java, v 0.1 2018年12月29日 15:31 yanhuai Exp $
 */
public class DaemonThreadFactory implements ThreadFactory {
    /** 线程名称前缀 */
    private final String            namePrefix;

    /** 线程计数器 */
    private final static AtomicLong COUNTER = new AtomicLong();

    /**
     * Construct a ThreadFactory instance.
     *
     * @param namePrefix 线程名称前缀
     */
    public DaemonThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + "-" + COUNTER.getAndIncrement());
        t.setDaemon(true);
        return t;
    }
}

