package com.allen.zookeeper.lock;

import com.allen.zookeeper.config.ZkUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @decription: 分布式锁测试
 * @author: 180449
 * @date 2021/8/7 9:45
 */
public class TestLock {

    ZooKeeper zk;

    @Before
    public void conn() {
        zk = ZkUtils.getZk();
    }

    @After
    public void close() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLock() {
        ExecutorService singleThreadPool = new ThreadPoolExecutor(
                10, 20,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new ThreadPoolExecutor.AbortPolicy());


        for (int i = 0; i < 10; i++) {
            singleThreadPool.execute(()-> {
                String threadName = Thread.currentThread().getName();
                WatchCallBack watchCallBack = new WatchCallBack();
                watchCallBack.setZk(zk);
                watchCallBack.setThreadName(threadName);
                watchCallBack.setWatchPath("/lock");
                // 1. 获得锁
                watchCallBack.tryLock();
                // 2. 业务操作
                System.out.println(threadName + " working ......");
                // 3. 释放锁
                watchCallBack.unLock();
            });
        }

        // singleThreadPool.shutdown();

        while (true) {

        }

    }

}
