package netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * NIO：
 * 阻塞：
 *  server.accept() 不再阻塞
 *  reader.readLine() 不再阻塞
 *
 * 问题：
 *  server.accept() 虽然不再阻塞，但是每次要遍历一次。C10K的时候，有可能一个连接都没有，但是任然在进行遍历，存在复杂度。
 *
 * 说明：
 *  当前也是但线程在允许，但是可以处理多个连接了。 当然，可以做多线程处理的。
 *
 * 解决：
 *   select 多路复用器（它只返回当前准备好的文件描述符。复杂度要低很多）
 *
 * 同步/异步：
 *  多路复用器虽然告诉你状态了。但是读写还是用户自己去触发===========>同步
 *  （它虽然告诉你当前有数据来了，但是它没有帮你把数据带到用户空间，还需要你自己去读。所以还是同步）
 */
public class SocketNIO {
    public static void main(String[] args) throws IOException, InterruptedException {

        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false); // 重点
        while (true) {
            Thread.sleep(1000);
            SocketChannel client = ss.accept(); // accept不在阻塞了。内核的支持
            if(client == null) {
                System.out.println("null....");
            } else {
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("client...port:" + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096); // 缓冲区可以在堆内也可以在堆外

            for(SocketChannel sc : clients) {
                int num = sc.read(buffer);
                if(num>0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(sc.socket().getPort() + ":" + b);
                    buffer.clear();

                }
            }
        }
    }
}
