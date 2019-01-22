package com.geckostroll.common.bizflow;

/**
 * 代表一个处理单元
 *
 * @author yanhuai
 * @version $Id: Entry.java, v 0.1 2018年08月08日 下午5:42 yanhuai Exp $
 */
public class Entry<T extends IBizProcContext> {

    /** 当前处理任务 */
    private final IBizProcess<T> processor;

    /** 当前处理任务执行结果是否是下一任务的前提条件 */
    private boolean              isNextPredication = false;

    /** 是否异步执行 */
    private boolean              async             = false;

    /** 下一个处理单元 */
    private Entry<T>             next;

    /** 当前节点失败后置处理 */
    private IBizProcess<T>       failPostProcessor;

    /**
     * 构造函数
     *
     * @param processor 处理器
     * @param async 是否异步
     */
    public Entry(IBizProcess<T> processor, boolean async) {
        this.processor = processor;
        this.async = async;
    }

    /**
     * 构造函数
     */
    public Entry(IBizProcess<T> processor, boolean isNextPredication,
                 IBizProcess<T> failPostProcessor) {
        this.processor = processor;
        this.isNextPredication = isNextPredication;
        this.failPostProcessor = failPostProcessor;
    }

    //============ Getter && Setter

    /**
     * Getter method for property <tt>next</tt>.
     *
     * @return property value of next
     */
    public Entry<T> getNext() {
        return next;
    }

    /**
     * Getter method for property <tt>failPostProcessor</tt>.
     *
     * @return property value of failPostProcessor
     */
    public IBizProcess<T> getFailPostProcessor() {
        return failPostProcessor;
    }

    /**
     * Setter method for property <tt>next</tt>.
     *
     * @param next value to be assigned to property next
     */
    public void setNext(Entry<T> next) {
        this.next = next;
    }

    /**
     * Getter method for property <tt>processor</tt>.
     *
     * @return property value of processor
     */
    public IBizProcess<T> getProcessor() {
        return processor;
    }

    /**
     * Getter method for property <tt>isNextPredication</tt>.
     *
     * @return property value of isNextPredication
     */
    public boolean isNextPredication() {
        return isNextPredication;
    }

    /**
     * Getter method for property <tt>async</tt>.
     *
     * @return property value of async
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Setter method for property <tt>async</tt>.
     *
     * @param async value to be assigned to property async
     */
    public void setAsync(boolean async) {
        this.async = async;
    }

}
