package com.geckostroll.common.bizflow;

import com.geckostroll.common.utils.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author yanhuai
 * @version $Id: BizProcessFlow.java, v 0.1 2018年12月29日 17:53 yanhuai Exp $
 */
public class BizProcessFlow<T extends IBizProcContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(BizProcessFlow.class);

    /**
     * 「处理任务栈」线程变量
     */
    private final ThreadLocal<Entry<T>>  processorStack = new ThreadLocal<Entry<T>>();

    /** 当前处理链名称 */
    private String            flowName;

    /**
     * 执行异步任务的线程池
     */
    private ExecutorService   asyncExecutor;

    /**
     * 开始处理链
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public BizProcessFlow<T> start() {
        processorStack.set(new Entry<T>(SingletonProcessor.getStarter(), false, null));
        return this;
    }

    /**
     * 添加处理节点，且当前节点的处理结果作为下一节点的前提条件
     *
     * @param processor
     * @return
     */
    public BizProcessFlow<T> then(IBizProcess<T> processor) {
        checkNotNull(processor);
        Entry<T> currEntry = checkNotNull(getCurrentEntry());
        currEntry.setNext(new Entry<T>(processor, true, null));
        return this;
    }

    /**
     * <p>添加处理节点，且当前节点的处理结果作为下一节点的前提条件
     * <p>当前节点的处理结果失败后可以绑定一个失败后置节点
     *
     * @param processor
     * @return
     */
    public BizProcessFlow<T> then(IBizProcess<T> processor, IBizProcess<T> failPostProcessor) {
        checkNotNull(processor);
        Entry<T> currEntry = checkNotNull(getCurrentEntry());
        currEntry.setNext(new Entry<T>(processor, true, failPostProcessor));
        return this;
    }

    /**
     * 添加异步处理节点
     *
     * @param processor
     * @return
     */
    public BizProcessFlow<T> async(IBizProcess<T> processor) {
        checkNotNull(processor);
        Entry<T> currEntry = checkNotNull(getCurrentEntry());
        currEntry.setNext(new Entry<T>(processor, true));
        return this;
    }

    /**
     * 添加处理节点，且当前节点的处理结果不影响后续节点的执行
     *
     * @param processor
     * @return
     */
    public BizProcessFlow<T> ignoreFail(IBizProcess<T> processor) {
        checkNotNull(processor);
        Entry<T> currEntry = checkNotNull(getCurrentEntry());
        currEntry.setNext(new Entry<T>(processor, false, null));
        return this;
    }

    /**
     * <p>添加处理节点，且当前节点的处理结果不影响后续节点的执行
     * <p>当前节点的处理结果失败后可以绑定一个失败后置节点
     *
     * @param processor
     * @return
     */
    public BizProcessFlow<T> ignoreFail(IBizProcess<T> processor, IBizProcess<T> failPostProcessor) {
        checkNotNull(processor);
        Entry<T> currEntry = checkNotNull(getCurrentEntry());
        currEntry.setNext(new Entry<T>(processor, false, failPostProcessor));
        return this;
    }

    /**
     * 开始执行处理链
     *
     * @param ctx
     * @return
     */
    public boolean onProcess(final T ctx) {
        boolean ret = false;
        try {
            checkNotNull(ctx);
            Entry<T> subEntry = checkNotNull(processorStack.get());
            Entry<T> entry = null;
            do {
                entry = subEntry;

                // proc
                boolean entryRet = false;
                final IBizProcess<T> proc = entry.getProcessor();
                if (proc != null) {
                    if (!entry.isAsync()) {
                        entryRet = proc.onProcess(ctx);
                    } else {
                        getAsyncExecutorOrDefault().execute(new Runnable() {
                            public void run() {
                                proc.onProcess(ctx);
                            }
                        });
                        entryRet = true;
                    }
                }

                //如果当前节点失败
                if (!entryRet) {
                    IBizProcess<T> failProcessor = entry.getFailPostProcessor();
                    //如果有指定失败后置处理，执行失败后置处理逻辑
                    if (failProcessor != null) {
                        failProcessor.onProcess(ctx);
                    }

                    //isNextPredication==true,那么短路后续所有节点
                    if (entry.isNextPredication()) {
                        break;
                    }
                }

                //find next
                subEntry = entry.getNext();
            } while (subEntry != null);

            ret = subEntry == null;

        } catch (Exception e) {
            LOGGER.error(String.format("%s,ctxId=%s,", this.flowName, ctx.getCtxId()), e);
        } finally {
            //务必清除线程变量
            processorStack.remove();
        }

        return ret;

    }

    /**
     * 取栈顶的一个entry。
     *
     * @return 最近的一个entry，如果不存在，则返回<code>null</code>
     */
    private Entry<T> getCurrentEntry() {
        Entry<T> subEntry = processorStack.get();
        Entry<T> entry = null;

        if (subEntry != null) {
            do {
                entry = subEntry;
                subEntry = entry.getNext();
            } while (subEntry != null);
        }

        return entry;
    }

    public ExecutorService getAsyncExecutorOrDefault() {
        if (this.asyncExecutor == null) {
            String executorName = StringUtils.defaultIfNull(this.flowName, "default");
            ThreadFactory tf = new BasicThreadFactory.Builder().namingPattern(executorName + "-async").daemon(true).build();
            this.asyncExecutor = new ThreadPoolExecutor(4, 12,
                    1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(5000), tf);
        }
        return this.asyncExecutor;
    }

    /**
     * Getter method for property <tt>flowName</tt>.
     *
     * @return property value of flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * Setter method for property <tt>flowName</tt>.
     *
     * @param flowName value to be assigned to property flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * Getter method for property <tt>asyncExecutor</tt>.
     *
     * @return property value of asyncExecutor
     */
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    /**
     * Setter method for property <tt>asyncExecutor</tt>.
     *
     * @param asyncExecutor value to be assigned to property asyncExecutor
     */
    public void setAsyncExecutor(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }
}
