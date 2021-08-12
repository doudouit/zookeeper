package com.allen.zookeeper.officialexample;

import com.allen.zookeeper.config.ZkUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @decription: 测试类
 * @author: 180449
 * @date 2021/8/11 16:07
 */
public class TestClass {

    ZooKeeper zk;

    @Before
    public void init() {
        zk = ZkUtils.getZk();
    }

    @Test
    public void test() throws IOException, KeeperException {
        String address = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183/testLock";
        // 监控的节点
        String znode = "/test";
        String fileName = "F://test.txt";
        String[] exec = {"1", "2"};
        Executor executor = new Executor(address, znode, fileName, exec);
        executor.run();
    }
}
