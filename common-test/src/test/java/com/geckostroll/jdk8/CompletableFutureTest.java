package com.geckostroll.jdk8;

import com.geckostroll.common.log.LogUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 *
 * @author yanhuai
 * @version $Id: CompletableFutureTest.java, v 0.1 2019年01月02日 14:59 yanhuai Exp $
 */
public class CompletableFutureTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(CompletableFutureTest.class);

    /**
     * CompletableFuture的基本使用
     */
    @Test
    public void test1() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("success");
        assertTrue(cf.isDone());
        // getNow方法返回计算结果或者null
        assertEquals("success", cf.getNow(null));
    }

    @Test
    public void test2() {
        // runAsync是异步方式，默认使用daemon线程执行Runnable任务
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            Assert.assertTrue(Thread.currentThread().isDaemon());
            sleep1s();
        });
        assertFalse(cf.isDone());
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            LOGGER.error("sleep interrupt", e);
        }
        assertTrue(cf.isDone());
    }

    /**
     * 测试同步执行动作
     */
    @Test
    public void test3() {
        CompletableFuture<String>cf = CompletableFuture.completedFuture("message").thenApply(s -> {
            assertFalse(Thread.currentThread().isDaemon());
            return s.toUpperCase();
        });
        assertEquals("MESSAGE", cf.getNow(null));
    }

    /**
     * 测试异步执行动作
     */
    @Test
    public void test4() {
        CompletableFuture<String>cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().isDaemon());
            sleep1s();
            return s.toUpperCase();
        });
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    public static void sleep1s() {
        try {
            Thread.sleep(1000);
            LogUtils.info(LOGGER, "run in other thread");
        } catch (Exception e) {
            LOGGER.error("sleep interrupt", e);
        }
    }

}
