package easychat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
    client_window client;
    boolean withInterface;

    ChatClientHandler(){
        withInterface = false;
    }
    ChatClientHandler(client_window client){
        this.client = client;
        withInterface = true;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        // just print message???0_o
        System.out.println(s);
        if (withInterface){
            client.incoming_msg.append(s + "\n");
        }
    }
}
