package com.allen.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        System.out.println("Hello World!");

        // zk是有session的概念的，没有连接池的概念
        //watch:观察，回调
        //watch的注册值发生在 读类型调用，get，exites。。。
        //第一类：new zk 时候，传入的watch，这个watch，session级别的，跟path 、node没有关系。
        // 因为连接需要时间，用 countDownLatch 阻塞，等待连接成功，控制台输出连接状态！
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", 4000, new Watcher() {

            // watch回掉方法
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println("path: " + path);
                System.out.println("new zk watch: " + event.toString());

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
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


                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });

        countDownLatch.await();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing...............");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed................");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
            default:
        }


        // 创建没有访问限制临时节点 数据是二进制安全的
        String pathname = zk.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        // 同步调用
        final Stat stat = new Stat();
        byte[] node = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("get data watch: " + event.toString());
                try {
                    //true   default Watch  被重新注册   new zk的那个watch
                    // zk.getData("/ooxx", true, stat);
                    zk.getData("/ooxx", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);

        System.out.println(new String(node));

        // 触发回调
        Stat stat1 = zk.setData("/ooxx", "newdata".getBytes(), 0);
        // 还会触发回调么? 不会 需要上面做一下递归调用监控watch的操作
        Stat stat2 = zk.setData("/ooxx", "newdata01".getBytes(), stat1.getVersion());
        // 相同的操作也会触发watch操作
        Stat stat3 = zk.setData("/ooxx", "newdata01".getBytes(), stat2.getVersion());

        // 异步调用
        System.out.println("-------async start----------");
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("-------async call back----------");
                System.out.println(ctx.toString());
                System.out.println(new String(data));
            }
        }, "asdf");
        System.out.println("-------async over----------");

        // 防止异步结果未取到，主线程结束，session连接过期，无法get到节点数据
        while (true) {

        }
    }
}
