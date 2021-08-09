package com.allen.zookeeper.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @decription:
 * @author: 180449
 * @date 2021/8/7 9:04
 */
public class DefaultWacth implements Watcher {

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {

        System.out.println("new zk event:" + event.toString());

        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                System.out.println("Disconnected...c...new");
                countDownLatch = new CountDownLatch(1);
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("connect...c...ok");
                countDownLatch.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
            default:

        }
    }
}
