package com.geckostroll.common.log;

import org.slf4j.Logger;

/**
 *
 * @author yanhuai
 * @version $Id: LogUtils.java, v 0.1 2018年12月27日 04:16 yanhuai Exp $
 */
public class LogUtils {

    /**
     * 打印debug日志。
     *
     * @param logger    日志对象
     * @param e         异常信息
     * @param objs      任意个要输出到日志的参数
     */
    public static void debug(Logger logger, Throwable e, Object... objs) {
        if (logger.isDebugEnabled()) {
            logger.debug(getLogString(objs), e);
        }
    }

    /**
     * 打印指定格式的info日志。
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void debug(Logger logger, String format, Object... objs) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, objs);
        }
    }

    /**
     * 打印info日志。
     *
     * @param logger    日志对象
     * @param e         异常信息
     * @param objs      任意个要输出到日志的参数
     */
    public static void info(Logger logger, Throwable e, Object... objs) {
        if (logger.isInfoEnabled()) {
            logger.info(getLogString(objs), e);
        }
    }

    /**
     * 打印指定格式的info日志。
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void info(Logger logger, String format, Object... objs) {
        if (logger.isInfoEnabled()) {
            logger.info(format, objs);
        }
    }

    /**
     * 打印warn日志。
     *
     * @param logger    日志对象
     * @param e         异常信息
     * @param objs      任意个要输出到日志的参数
     */
    public static void warn(Logger logger, Throwable e, Object... objs) {
        logger.warn(getLogString(objs), e);
    }

    /**
     * 打印指定格式的warn日志。
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void warn(Logger logger, String format, Object... objs) {
        logger.warn(format, objs);
    }

    /**
     * 打印error日志。
     *
     * @param logger    日志对象
     * @param e         异常信息
     * @param objs      任意个要输出到日志的参数
     */
    public static void error(Logger logger, Throwable e, Object... objs) {
        logger.error(getLogString(objs), e);
    }

    /**
     * 打印指定格式的error日志。
     *
     * @param logger
     * @param format
     * @param objs
     */
    public static void error(Logger logger, String format, Object... objs) {
        logger.error(format, objs);
    }

    /**
     * 生成输出到日志的字符串
     *
     * @param objs      任意个要输出到日志的参数
     * @return          日志字符串
     */
    public static String getLogString(Object... objs) {
        StringBuilder log = new StringBuilder();

        if (objs != null) {
            for (Object o : objs) {
                log.append(o);
            }
        }
        return log.toString();
    }


}
