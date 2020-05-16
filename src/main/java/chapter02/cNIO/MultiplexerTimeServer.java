package chapter02.cNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor模型的朴素原型
 */
public class MultiplexerTimeServer implements  Runnable {

     private Selector selector;
     private ServerSocketChannel serverSocketChannel;
     private volatile boolean stop;

     public MultiplexerTimeServer(int port) {

         try {
             // 步骤一、打开ServerSocketChannel，用于监听客户端的连接，它是所有客户端连接的父管道
             serverSocketChannel = ServerSocketChannel.open();

             //步骤二、绑定监听端口、设置非阻塞
             serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
             serverSocketChannel.configureBlocking(false);

             // 步骤三、创建多路复用器
             selector= Selector.open();

             // 将通道注册到selector，监听SelectionKey.OP_ACCEPT事件
             serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     public void stop() {
         this.stop=true;
     }


    public void run() {

         while(!stop) {
             try {
                 // 循环遍历selector，它的休眠时间为1s，无论是否有读写等时间发生，selecter没个1s都被唤醒一次
                 selector.select(1000);
                 // 当有就绪状态的channal时，selector将返回就绪状态的ChannelKey集合
                 Set<SelectionKey> selectedKeys = selector.selectedKeys();
                 Iterator<SelectionKey> it = selectedKeys.iterator();
                 SelectionKey key = null;
                 while (it.hasNext()) {
                     key = it.next();
                     it.remove();
                     try {
                         handleInput(key);
                     } catch (Exception e) {
                         if(key != null) {
                             key.cancel();
                             if(key.channel() != null) {
                                 key.channel().close();
                             }
                         }
                     }
                 }
             } catch (Throwable t) {
                 t.printStackTrace();
             }
         }

         // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
         if(selector != null) {
             try {
                 selector.close();
             }catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }

    /**
     * 处理SelectionKey
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
         if(key.isValid()) {

             // 接受就绪
             if(key.isAcceptable()) {
                 // 若接受的事件是“接收就绪” 操作,就获取客户端连接
                 ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                 SocketChannel sc = ssc.accept(); // 三次握手完成
                 // 切换为非阻塞模式
                 sc.configureBlocking(false);
                 // 将该通道注册到selector选择器上
                 sc.register(selector, SelectionKey.OP_READ);
             }

             // 读就绪
             if(key.isReadable()) {
                 // 获取该选择器上的“读就绪”状态的通道
                 SocketChannel sc=(SocketChannel) key.channel();
                 // 读取数据
                 ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                 int readBytes = sc.read(readBuffer);
                 if(readBytes > 0) {
                     readBuffer.flip();
                     byte[] bytes = new byte[readBuffer.remaining()];
                     readBuffer.get(bytes);
                     String body = new String(bytes, "UTF-8");
                     System.out.println("the time server receive order : " + body);
                     String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                     doWrite(sc, currentTime);
                 } else if(readBytes < 0) {
                     key.cancel();
                     sc.close();
                 } else {
                     ; // 读到0字符，忽略
                 }
             }
         }
    }

    /**
     * 将应答消息异步发给客户端
     * @param channel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if(response != null && response.trim().length() >0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            // put操作将数据复制到缓冲区中
            writeBuffer.put(bytes);
            // 然后对缓冲区进行flip操作
            writeBuffer.flip();
            // 最后调用SocketChannel的write方法将缓冲区的字节数组发送出去
            // 注意：由于SocketChnnel是异步非阻塞的，它并不能宝藏一次就能够吧需要发送的字节发送完，此时会出现“写半包”问题。
            // 我们需要注册写操作，不断轮询Selector讲没有发送玩的字节发送完，可以通过ByteBuffer的hasRemaining()方法判断消息是否发送完成。
            // 此处仅仅演示，没有处理写半包的问题。
            channel.write(writeBuffer);
        }
    }
}
