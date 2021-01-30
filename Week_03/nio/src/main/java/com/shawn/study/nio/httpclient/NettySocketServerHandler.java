package com.shawn.study.nio.httpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettySocketServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.printf("message: %s \n", msg.toString());
    ctx.channel().writeAndFlush("返回信息给客户端：" + msg);
  }

}
