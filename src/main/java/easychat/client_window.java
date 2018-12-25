package easychat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class client_window {
    private JButton send;
    JTextArea incoming_msg;
    private JTextField print_msg;
    private JPanel rootPanel;

    private String message;
    private Channel channel;
    private String address;
    private String name;

    public void setAddress(String address) {
        this.address = address;
    }

    public client_window() {
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        print_msg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
    }

    public void sendMessage() {
        message = print_msg.getText();
        incoming_msg.append("You: " + message + "\n");
        print_msg.setText("");

        if ("\\name".equals(message)) {
//                        incoming_msg.append("Type your name:\n");
            channel.writeAndFlush("\\name " + message + "\r\n");
            incoming_msg.append("Your name is: " + message + " now\r\n");
        } else if ("\\exit".equals(message)) {
            System.out.println("closing app");
            channel.writeAndFlush(message + "\r\n");
            try {
                channel.closeFuture().await();
            } catch (InterruptedException e) {
                System.out.println("exception caught");
                e.printStackTrace();
            }
        } else {
            channel.writeAndFlush(message + "\r\n");
        }
    }

    public static void main(String[] args) {
        client_window client = new client_window();

        JFrame frame = new JFrame("client_window");
        frame.setContentPane(client.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        client.speakingToServer(client);
    }

    private void speakingToServer(client_window client) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    // pipeline below
                    .handler(new ChatClientInitializer(client));
            while (address == null) {
                dialogWithClient a = new dialogWithClient(client);
                a.run(client);
                if (address == null)
                    System.out.println("error");
            }

            client.channel = b.connect(address, 8000).sync().channel();
            System.out.println("now you can input");
            while (true) { }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
