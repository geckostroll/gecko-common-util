package com.geckostroll.common.concurrent;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.*;

/**
 * <p> {@link Executor} 构造器,可以按需定制{@link Executor}的各个配置参数，
 *     没有设置的参数均有通用默认值。
 *
 * <p>Usage example: <pre>   {@code
 *
 *      ExecutorService executor = ExecutorBuilder.newBuilder()
 *                                                .executorName("foo-executor")
 *                                                .corePoolSize(10)
 *                                                .maxPoolSize(20)
 *                                                .queueCapacity(1000)
 *                                                .keepAliveTime(60)
 *                                                .timeUnit(TimeUnit.SECONDS)
 *                                                .buildExecutor();
 * }</pre>
 */
public class ExecutorBuilder {


    //============================== 构造参数
    private static final int                 UNSET_INT              = -1;

    private static final String              UNSET_STR              = "";

    private static final Boolean             UNSET_BOOL             = null;

    private static final int      DEFAULT_POOL_SIZE      = Runtime.getRuntime().availableProcessors();
    private static final int      DEFAULT_KEEP_TIME      = 60;
    private static final TimeUnit DEFAULT_TIME_UNIT      = TimeUnit.SECONDS;
    private static final int      DEFAULT_QUEUE_CAPACITY = 1000;
    private static final String   DEFAULT_EXECUTOR_NAME  = "default-worker";

    /** 线程池核心线程数 */
    private   int                      corePoolSize           = UNSET_INT;
    /** 线程池最大线程数 */
    private   int                      maxPoolSize            = UNSET_INT;
    /**  线程淘汰时间，默认 60s */
    private   int                      keepAliveTime          = UNSET_INT;
    /** 线程淘汰时间单位，默认秒 */
    protected TimeUnit                 timeUnit;
    /** 任务队列大小 */
    private   int                      queueCapacity          = UNSET_INT;
    /** 默认使用 "default-worker" */
    private   String                   executorName           = UNSET_STR;
    /** 默认开启 idle 线程销毁 */
    private   Boolean                  allowCoreThreadTimeout = UNSET_BOOL;
    /** 默认使用 {@link DaemonThreadFactory} */
    private   ThreadFactory            threadFactory;
    /** 默认使用 {@link DefaultAbortPolicy} */
    private   RejectedExecutionHandler rejectedExecutionHandler;
    /** 工作队列 */
    private   BlockingQueue<Runnable>  workQueue;

    /**
     * private construct
     */
    private ExecutorBuilder() {
    }

    /**
     * new builder
     *
     * @return
     */
    public static ExecutorBuilder newBuilder() {
        return new ExecutorBuilder();
    }

    /***
     * 设置线程池的核心线程数。
     *
     * @param corePoolSize 核心线程数
     * @return Builder
     */
    public ExecutorBuilder corePoolSize(int corePoolSize) {
        checkState(this.corePoolSize == UNSET_INT, "corePoolSize was already set to %s",
                this.corePoolSize);
        checkArgument(corePoolSize > 0, "corePoolSize must be positive");
        checkArgument(this.maxPoolSize == UNSET_INT || corePoolSize < this.maxPoolSize,
                "corePoolSize must be less than maxPoolSize");
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * 设置线程池的最大线程数。
     *
     * @param maxPoolSize 线程池的最大线程数
     * @return Builder
     */
    public ExecutorBuilder maxPoolSize(int maxPoolSize) {
        checkState(this.maxPoolSize == UNSET_INT, "maxPoolSize was already set to %s",
                this.maxPoolSize);
        checkArgument((maxPoolSize > 0 && maxPoolSize >= corePoolSize),
                "maxPoolSize must be positive and greater or equal to corePoolSize");
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * IDLE 线程的存活时间
     *
     * @param keepAliveTime IDLE 线程的存活时间
     * @return Builder
     */
    public ExecutorBuilder keepAliveTime(int keepAliveTime) {
        checkState(this.keepAliveTime == UNSET_INT, "keepAliveTime was already set to %s",
                this.keepAliveTime);
        checkArgument(keepAliveTime > 0, "keepAliveTime must be positive");
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * IDLE 线程的存活时间单位，默认为秒。
     *
     * @param timeUnit 时间单位
     * @return Builder
     */
    public ExecutorBuilder timeUnit(TimeUnit timeUnit) {
        checkState(this.timeUnit == null, "timeUnit was already set to %s", this.timeUnit);
        checkNotNull(timeUnit);
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * 线程池工作队列的大小。
     *
     * @param queueCapacity 工作队列大小
     * @return Builder
     */
    public ExecutorBuilder queueCapacity(int queueCapacity) {
        checkState(this.queueCapacity == UNSET_INT, "queueCapacity was already set to %s",
                this.queueCapacity);
        checkArgument(queueCapacity > 0, "queueCapacity must be positive");
        this.queueCapacity = queueCapacity;
        return this;
    }

    /**
     * 线程池的名称
     *
     * @param executorName 线程池的名称
     * @return Builder
     */
    public ExecutorBuilder executorName(String executorName) {
        checkState(UNSET_STR.equals(this.executorName), "executorName was already set to %s",
                this.executorName);
        checkArgument(StringUtils.isNotBlank(executorName), "executorName must not be null");
        this.executorName = executorName;
        return this;
    }

    /**
     * 是否允许 IDLE 线程被销毁。
     *
     * @param allowCoreThreadTimeout boolean
     * @return Builder
     */
    public ExecutorBuilder allowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
        checkState(this.allowCoreThreadTimeout == UNSET_BOOL,
                "allowCoreThreadTimeout was already set to %s", this.allowCoreThreadTimeout);
        this.allowCoreThreadTimeout = allowCoreThreadTimeout;
        return this;
    }

    /**
     * 线程工厂，默认使用 {@link DaemonThreadFactory}
     *
     * @param threadFactory 线程工厂
     * @return Builder
     */
    public ExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        checkState(this.threadFactory == null);
        this.threadFactory = checkNotNull(threadFactory);
        return this;
    }

    /**
     * 线程池任务丢弃策略。
     *
     * @param rejectedExecutionHandler 线程池任务丢弃策略
     * @return Builder
     */
    public ExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        checkState(this.rejectedExecutionHandler == null);
        this.rejectedExecutionHandler = checkNotNull(rejectedExecutionHandler);
        return this;
    }


    /**
     * build {@link ThreadPoolExecutor}
     *
     * @return
     */
    public ExecutorService buildExecutor() {
        ThreadPoolExecutor executor =  new ThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                getKeepAliveTime(), getTimeUnit(), getWorkQueue(), getThreadFactory(), getRejectedExecutionHandler());
        executor.allowCoreThreadTimeOut(getAllowCoreThreadTimeout());
        return executor;
    }

    /**
     * build {@link ListeningExecutorService}
     *
     * @return
     */
    public ListeningExecutorService buildListeningExecutor() {
        return MoreExecutors.listeningDecorator(buildExecutor());
    }

    /**
     * build {@link ScheduledExecutorService}
     *
     * @return
     */
    public ScheduledExecutorService buildScheduledExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(getCorePoolSize(), getThreadFactory(),
                getRejectedExecutionHandler());
        executor.setMaximumPoolSize(getMaxPoolSize());
        executor.setKeepAliveTime(getKeepAliveTime(), getTimeUnit());
        executor.allowCoreThreadTimeOut(getAllowCoreThreadTimeout());
        return executor;
    }

    /**
     * Default abort policy
     *
     * @author zuhongcai
     * @version $Id: ConfigurableExecutorImpl.java, v 0.1 Dec 30, 2014 5:11:59 PM zuhongcai Exp $
     */
    private static class DefaultAbortPolicy extends BaseThreadAbortPolicy {

        public DefaultAbortPolicy(String executorName) {
            super(executorName);
        }

        @Override
        protected void rejectHandle() {
            throw new RejectedExecutionException("reject task,executorName = " + executorName + ", reject count = " + getRejectCounter());
        }
    }

    /**
     * 获取核心线程数，默认值为{@link #DEFAULT_POOL_SIZE}
     *
     * @return property value of corePoolSize
     */
    public int getCorePoolSize() {
        return (corePoolSize == UNSET_INT) ? DEFAULT_POOL_SIZE : corePoolSize;
    }

    /**
     * 获取最大线程数，默认值为{@link #DEFAULT_POOL_SIZE}
     *
     * @return property value of maxPoolSize
     */
    public int getMaxPoolSize() {
        return (maxPoolSize == UNSET_INT) ? DEFAULT_POOL_SIZE : maxPoolSize;
    }

    /**
     * IDLE 线程的存活时间，默认为 60s。
     *
     * @return property value of keepAliveTime
     */
    public int getKeepAliveTime() {
        return (keepAliveTime == UNSET_INT) ? DEFAULT_KEEP_TIME : keepAliveTime;
    }

    /**
     * IDLE 线程的存活时间单位，默认为 s
     *
     * @return property value of timeUnit
     */
    public TimeUnit getTimeUnit() {
        return (timeUnit == null) ? DEFAULT_TIME_UNIT : timeUnit;
    }

    /**
     * 任务队列大小,默认1000
     *
     * @return property value of queueCapacity
     */
    public int getQueueCapacity() {
        return (queueCapacity == UNSET_INT) ? DEFAULT_QUEUE_CAPACITY : queueCapacity;
    }

    /**
     * 执行器的名称,默认 "default-worker"
     *
     * @return property value of executorName
     */
    public String getExecutorName() {
        return StringUtils.isBlank(executorName) ? DEFAULT_EXECUTOR_NAME : executorName;
    }

    /**
     * 默认开启 idle 线程销毁
     *
     * @return property value of allowCoreThreadTimeout
     */
    public Boolean getAllowCoreThreadTimeout() {
        return MoreObjects.firstNonNull(allowCoreThreadTimeout, Boolean.FALSE);
    }

    /**
     * 默认使用 {@link DaemonThreadFactory}
     *
     * @return property value of threadFactory
     */
    public ThreadFactory getThreadFactory() {
        return firstNonNull(threadFactory, new DaemonThreadFactory(getExecutorName()));
    }

    /**
     * 默认使用 {@link DefaultAbortPolicy}
     *
     * @return property value of rejectedExecutionHandler
     */
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return firstNonNull(rejectedExecutionHandler, new DefaultAbortPolicy(getExecutorName()));
    }

    /**
     * Getter method for property <tt>workQueue</tt>.
     *
     * @return property value of workQueue
     */
    public BlockingQueue<Runnable> getWorkQueue() {
        return firstNonNull(workQueue, new LinkedBlockingQueue<Runnable>(getQueueCapacity()));
    }



}
