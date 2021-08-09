package com.allen.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @decription: 分布式配置中心测试
 * @author: 180449
 * @date 2021/8/7 9:11
 */
public class TestConf {

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
    public void testConf() {
        MyConf myConf = new MyConf();

        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZk(zk);
        watchCallBack.setMyConf(myConf);
        watchCallBack.setWatchPath("/AppConf");

        watchCallBack.await();

        while (true) {
            if ("".equals(myConf.getConf())) {
                System.out.println("conf diu le!!!");
                watchCallBack.await();
            } else {
                System.out.println(myConf.getConf());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
