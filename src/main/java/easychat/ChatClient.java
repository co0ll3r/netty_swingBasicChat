package easychat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClient {
    private final String host;
    private final int port;

    public static void main(String[] args) throws Exception {
        System.out.println("input an ip address");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String ipHost = in.readLine();// input.next();
        if ("1".equals(ipHost)) {
            ipHost = "localhost";
            System.out.println("ip = localhost");
        } else if ("2".equals(ipHost)) {
            ipHost = "192.168.1.248";
            System.out.println("ip = 192.168.1.248");
        }
        new ChatClient(ipHost, 8000).run();
    }

    public ChatClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String message = "\\name";
        try {

            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    // pipeline below
                    .handler(new ChatClientInitializer());
            Channel channel = b.connect(host, port).sync().channel();

            while (true) {
                if ("\\name".equals(message)) {
                    System.out.println("type your name:");
                    message = in.readLine();
                    channel.writeAndFlush("\\name " + message + "\r\n");
                    continue; // need it?
                }
                System.out.print("You: ");
                message = in.readLine();

                channel.writeAndFlush(message + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
