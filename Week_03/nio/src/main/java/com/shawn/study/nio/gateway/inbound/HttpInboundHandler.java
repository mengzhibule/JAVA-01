package com.shawn.study.nio.gateway.inbound;

import com.shawn.study.nio.gateway.filter.HeaderHttpRequestFilter;
import com.shawn.study.nio.gateway.filter.HttpRequestFilter;
import com.shawn.study.nio.gateway.outbound.HttpOutBoundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

  private final List<String> proxyServer;
  private HttpOutBoundHandler handler;
  private HttpRequestFilter filter = new HeaderHttpRequestFilter();

  public HttpInboundHandler(List<String> proxyServer) {
    this.proxyServer = proxyServer;
    this.handler = new HttpOutBoundHandler(this.proxyServer);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      System.out.printf("channelRead流量接口请求开始，msg\n", msg.toString());
      FullHttpRequest fullRequest = (FullHttpRequest) msg;

      handler.handle(fullRequest, ctx, filter);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

}
