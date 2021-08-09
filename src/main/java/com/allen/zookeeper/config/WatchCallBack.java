package com.allen.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @decription: 自定义回调
 * @author: 180449
 * @date 2021/8/7 9:17
 */
public class WatchCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    private ZooKeeper zk;

    private String watchPath;

    private MyConf myConf;

    private CountDownLatch init = new CountDownLatch(1);

    public void setWatchPath(String watchPath) {
        this.watchPath = watchPath;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void await() {
        try {
            zk.exists(watchPath, this, this, "initExists");
            init.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 元数据回调
     * @param rc
     * @param path
     * @param ctx
     * @param data
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            myConf.setConf(new String(data));
            init.countDown();
        }
    }

    /**
     * 事件监听
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println("node event: " + event.toString());

        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                // 节点创建
                System.out.println("...watch@Created");
                zk.getData(watchPath, this, this, "NodeCreated");
                break;
            case NodeDeleted:
                // 节点删除
                System.out.println("...watch@Delete");
                myConf.setConf("");
                // zk.exists(watchPath, this, this, "asdf");
                init = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                // 节点修改
                System.out.println("...watch@Update");
                zk.getData(watchPath, this, this, "NodeChanged");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
            default:

        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData(watchPath, this, this, "ndoe-exists");
        }
    }
}
