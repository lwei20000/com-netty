package chapter02.bAIO_FAKE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 由于in和out还是采用的输入输出流，他的读和写都是同步阻塞的。
 * 仅仅加了一个线程池，并不能从根本上解决问题。
 * 所以这是一个伪异步
 *
 * 效果是：当有多个线程一起涌入的时候，不需要排队，可以由线程池接受多个请求
 */
public class TimeServer {
    private  int po;

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if(args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //
            }
        }

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;

//            while (true) {
//                socket = server.accept();
//                System.out.println("===================");
//                new Thread(new TimeServerHandler(socket)).start();
//            }

            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(5,1000);
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}

// 注意：
// JvisualVM打印线程堆栈的方法：
// C:\Program Files\Java\jdk1.7.0_80\bin>jvisualvm.exe
// 找到对应线程：右键Thread Dump
