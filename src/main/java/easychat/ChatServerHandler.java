package easychat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    // took executor from the comment to the video
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private HashMap<String, String> userTable = new HashMap();

    // regExp
    Matcher matcher;
    Pattern nameCommand = Pattern.compile("(^\\\\name\\s)(.*)"),
            psCommand = Pattern.compile("(^\\\\ps\\s)(.+)\\s(.*)");


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[СЕРВАК] - " + incoming.remoteAddress() + " присоединился\n");
        }

//        userTable.put(incoming.remoteAddress(), "");
        channels.add(ctx.channel());
        incoming.writeAndFlush("Welcome, you successfully connected\r\n");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush("[СЕРВАК] - " + incoming.remoteAddress() + " отсоединился\n");
        }

        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel incoming = channelHandlerContext.channel();
        String incomingMessage = s;
        System.out.println("[" + incoming.remoteAddress() + "} " + incomingMessage);

        matcher = nameCommand.matcher(incomingMessage); // "(^\\\\name\\s)(.*)"
        // change name command
        if (matcher.find()) {
            System.out.println("changed name: " + incoming.remoteAddress() + " " + matcher.group(2));
            userTable.put(String.valueOf(incoming.remoteAddress()), matcher.group(2));

        } else if ("\\list".equals(incomingMessage)) {
            System.out.println("list command");

            for (Map.Entry<String, String> stringStringEntry : userTable.entrySet()) {
                System.out.println(stringStringEntry);
            }
///            System.out.println(Arrays.toString(userTable.entrySet().toArray()));
/*            int i = 0;
            StringBuilder userList = new StringBuilder();
            for (Channel channel : channels) {
                userList.append("User ").append(++i).append(" ip = ").append(incoming.remoteAddress()).append("/\n");
            }
            for (Channel channel : channels) {
                channel.writeAndFlush(userList.toString());
            }*/
        } else if ("\\exit".equals(incomingMessage)){
            System.out.println("[" + incoming.remoteAddress() + "] решил уйти\n");
            incoming.closeFuture().sync();
            handlerRemoved(channelHandlerContext);
        }
//        if ("\\ps")
        // sending a message to everybody, except the sender
        else
        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
            }
        }
    }
}
