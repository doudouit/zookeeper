package com.allen.zookeeper.config;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @decription: zookeeper工具类
 * @author: 180449
 * @date 2021/8/7 8:59
 */
public class ZkUtils {


    private static ZooKeeper zk;

    // private static String address = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183/testConf";
    private static String address = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183/testLock";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static DefaultWacth defaultWacth = new DefaultWacth();

    public static ZooKeeper getZk() {
        try {
            zk = new ZooKeeper(address, 3000, defaultWacth);
            defaultWacth.setCountDownLatch(countDownLatch);
            // 将主线程阻塞住，避免zk还未初始化完成就返回
            countDownLatch.await();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return zk;
    }
}
