package netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多路复用多线程版
 */
public class SocketMultiplexingThreads {

    private ServerSocketChannel server = null;
    private Selector selector1 = null;
    private Selector selector2 = null;
    private Selector selector3 = null;
    int port = 9090;

    public void initServer() throws IOException {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));

        selector1 = Selector.open();
        selector2 = Selector.open();
        selector3 = Selector.open();

        // 吧server注册到1上面
        server.register(selector1, SelectionKey.OP_ACCEPT);
    }

    // main
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketMultiplexingThreads service = new SocketMultiplexingThreads();
        service.initServer();
        NioThread T1 = new NioThread(service.selector1, 2);  // boss

        NioThread T2 = new NioThread(service.selector2);   // worker
        NioThread T3 = new NioThread(service.selector3);   // worker

        T1.start();
        Thread.sleep(1000);
        T2.start();
        T3.start();

        System.out.println("服务器启动了。。。。。。");
    }
}

/**
 * 线程类
 */
class NioThread extends Thread {
    Selector selector = null;
    static int selectors = 0;

    int id = 0;
    boolean boss = false;

    static BlockingQueue<SocketChannel>[] queue;

    static AtomicInteger idx = new AtomicInteger();

    //boss用
    NioThread(Selector sel, int n) {
        this.selector = sel;
        this.selectors = n;  // 2
        this.boss = true;

        queue = new LinkedBlockingDeque[selectors];
        for(int i = 0; i < n; i++) {
            queue[i] = new LinkedBlockingDeque();
        }
        System.out.println("Boss 启动");
    }

    // worker用
    NioThread(Selector sel) {
        this.selector = sel;
        id = idx.getAndIncrement() % selectors; // id=0 or 1.
        System.out.println("worker :" + id +"启动");
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (selector.select(10) > 0) {
                    Set<SelectionKey> selectionKey = selector.selectedKeys(); // 从多路复用器中取出有效的key
                    Iterator<SelectionKey> iter = selectionKey.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if(key.isAcceptable()) { // 连接
                            acceptHandler(key);
                        } else if(key.isReadable()) { // 读取
                            readHandler(key);
                        }
                    }
                }
                if(!boss && !queue[id].isEmpty()) { // boss不参与的。你有三个线程，boss不参与，只有worker更具分配，分别注册自己的client
                    ByteBuffer buffer = ByteBuffer.allocate(8192);
                    SocketChannel client = queue[id].take();
                    client.register(selector, SelectionKey.OP_READ, buffer);
                    System.out.println("--------------------------------------");
                    System.out.println("新客户端：" + client.socket().getPort() + "分配到worker： " + (id));
                    System.out.println("--------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // acceptHandler处理连接事件
    public void acceptHandler(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);

        int num = idx.getAndDecrement() % selectors; // 0 or 1
        queue[num].add(client);
    }

    // readHandler处理读事件
    public void readHandler(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel)key.channel();
        ByteBuffer buffer = (ByteBuffer)key.attachment();

        buffer.clear();
        int read = 0;
        while (true) {
            read = client.read(buffer);
            if(read > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    client.write(buffer);
                }
                buffer.clear();
            } else if(read == 0) {
                break;
            } else { // -1:有可能是客户端close_wait: bug死循环
                client.close();
                break;
            }
        }
    }
}
