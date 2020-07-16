package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 查看线程数的工具：
 *    C:\Program Files\Java\jdk1.8.0_131\bin\jvisualvm.exe
 */
public class NettyIO {
    public static void main(String[] args) throws Exception {

        // 此处的试验：
        //
        NioEventLoopGroup boss = new NioEventLoopGroup(2); // boss组里面的线程数
        NioEventLoopGroup worker = new NioEventLoopGroup(2); // worker组里面的线程数
        ServerBootstrap boot = new ServerBootstrap();

        try {
            boot.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new MyInbound());
                        }
                    });

            ChannelFuture future = boot.bind(9090).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * MyInbound类
 */
class MyInbound extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
