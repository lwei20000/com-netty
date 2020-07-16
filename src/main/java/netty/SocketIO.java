package netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 跑本服务
 * 在xshell工具下输入：nc 127.0.0.1 9090
 * 在windows下安装nc: https://eternallybored.org/misc/netcat/
 *                   将nc.exe 复制到C:\Windows\System32的文件夹下
 *
 * 阻塞：
 *   server.accept()阻塞一
 *   reader.readLine() 阻塞二
 *
 * 运行结果说明：
 *   第一次 nc 127.0.0.1 9090后阻塞，等待客户端输入。
 *   第二次nc 127.0.0.1 9090后没有反应，因为 while (true) 没有返回。
 *
 * 问题：
 *   这个模式根本无法用，智能连接一个客户端
 *
 * 解决：
 *   取得一个连接之后就抛出一个线程，这样就可以处理多个连接。
 */
public class SocketIO {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);

        System.out.println("step1: new ServerSocket(9090)");

        Socket client = server.accept();// 阻塞1

        System.out.println("step2:client\t" + client.getPort());

        InputStream in = client.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        System.out.println(reader.readLine());// 阻塞2

        while (true) {

        }

    }
}
