package com.shawn.study.nio.httpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettySocketClientHandler extends SimpleChannelInboundHandler<String> {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.channel().writeAndFlush("向客户端发送消息");
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s)
      throws Exception {
    System.out.println(s);
  }
}
