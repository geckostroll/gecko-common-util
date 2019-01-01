package com.geckostroll.common.bizflow;

/**
 *
 * @author yanhuai
 * @version $Id: IBizProcess.java, v 0.1 2018年08月08日 下午5:43 yanhuai Exp $
 */
public interface IBizProcess<T extends IBizProcContext> {

    /**
     * 处理任务的唯一ID
     *
     * @return
     */
    String getId();

    /**
     * 执行任务
     *
     * @param ctx
     * @return
     */
    boolean onProcess(T ctx);

    /**
     * 任务失败时返回的{@code Response}
     *
     * @return
     */
    Object getFakeResponse(T ctx);
}
