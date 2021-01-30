package com.shawn.study.nio.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter{

  @Override
  public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
    fullRequest.headers().set("msg", "request filter");
  }
}
