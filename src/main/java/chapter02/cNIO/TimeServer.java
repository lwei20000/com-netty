package chapter02.cNIO;

import java.io.IOException;

/**
 * NIO的包是在JDK1.4中引入的。NIO弥补了原来同步阻塞IO的不足。
 * 1、缓冲区buffer
 * 2、通道channel
 * 3、多路复用器Selector
 * 
 *
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if(args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //
            }
        }

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultipexerTimeServer-001").start();
    }
}
