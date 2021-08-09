package com.allen.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @decription: 自定义分布式锁处理类
 * @author: 180449
 * @date 2021/8/7 9:46
 */
public class WatchCallBack implements AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback, Watcher {

    private ZooKeeper zk;

    private String threadName;

    private String pathName;

    private String watchPath;

    private CountDownLatch cc = new CountDownLatch(1);

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void setWatchPath(String watchPath) {
        this.watchPath = watchPath;
    }



    public void tryLock() {
        try {
            System.out.println(threadName + " created.......");
            zk.create(watchPath, threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName + " work over .........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stringcallback
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name!=null) {
            System.out.println(threadName + " create node " + name);
            pathName = name;
            zk.getChildren("/", false, this, "asdf");
        }
    }

    /**
     * child2callback
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));

        // 判断当前序列是否是第一个
        if (i == 0) {
            try {
                System.out.println(threadName + " 的节点 " + pathName + " is first");
                // todo 这步干嘛的，不太明白
                zk.setData("/", threadName.getBytes(), -1);
                cc.countDown();
            }  catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 判断当前节点的上一个节点是否存在
            // 监控每个节点的上一个节点
            zk.exists("/"+children.get(i-1), this, this, "zxc");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        // 偷懒
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());

        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "asdf");
                break;
            case NodeDataChanged:
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
}
