package com.geckostroll.common.concurrent;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author yanhuai
 * @version $Id: ThreadAbortPolicy.java, v 0.1 2018年12月29日 15:30 yanhuai Exp $
 */
public abstract class BaseThreadAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

    /** counter */
    private final AtomicLong rejectCounter = new AtomicLong(0);

    /** executor name  */
    protected final String   executorName;

    /**
     * @param executorName
     */
    public BaseThreadAbortPolicy(String executorName) {
        super();
        this.executorName = executorName;
    }

    /**
     * Getter method for property <tt>executorName</tt>.
     *
     * @return property value of executorName
     */
    public String getExecutorName() {
        return executorName;
    }

    /**
     * Getter method for property <tt>rejectCounter</tt>.
     *
     * @return property value of rejectCounter
     */
    public Long getRejectCounter() {
        return rejectCounter.get();
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        rejectCounter.incrementAndGet();
        rejectHandle();
    }

    /**
     * reject handle
     */
    protected abstract void rejectHandle();

}
