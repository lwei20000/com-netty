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

/**
 * 多路复用单线程版
 */
public class SocketMultiplexingSingleThreadv1 {

    // NIO: N=nonblocking, socket网络内核机制
    // NIO: N=new, JDK(channel, byteBuffer, selector多路复用器)

    private ServerSocketChannel server = null;
    private Selector selector = null;
    int port = 9090;

    public void initServer() throws IOException {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        initServer();
        System.out.println("服务器启动了...");
        while (true) {
            while (selector.select(0) > 0) { // 问过内核了有没有事件，内核回复有。
                Set<SelectionKey> selectionKey = selector.selectedKeys(); // 从多路复用器中取出有效的key

                Iterator<SelectionKey> iter = selectionKey.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if(key.isAcceptable()) { // 连接
                        acceptHandler(key);
                    } else if(key.isReadable()) { // 读取
                        System.out.println("一般用户数据到达会触发，但是啥时候会疯狂触发呢？");
                        readHandler(key);
                    }
                }
            }
        }
    }

    // acceptHandler处理连接事件
    public void acceptHandler(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        client.register(selector, SelectionKey.OP_READ, buffer);
        System.out.println("--------------------------------------");
        System.out.println("新客户端：" + client.getRemoteAddress());
        System.out.println("--------------------------------------");
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


    /**
     * main
     */
    public static void main(String[] args) throws IOException {
        SocketMultiplexingSingleThreadv1 service = new SocketMultiplexingSingleThreadv1();
        service.start();
    }
}
