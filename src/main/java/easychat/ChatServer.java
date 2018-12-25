package easychat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private final int port;

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(8000).run();
    }

    public ChatServer(int port){
        this.port = port;
    }

    public  void run() throws InterruptedException{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // it's where pipeline comes and makes an income
                    .childHandler((new ChatServerInitializer()));
            // wtf is this
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("server started");
            future.channel().closeFuture().sync();
            System.out.println("server started2");
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
        System.out.println("server closed");
    }
}
