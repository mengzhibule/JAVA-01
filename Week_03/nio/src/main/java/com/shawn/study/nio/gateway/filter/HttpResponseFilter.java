package com.shawn.study.nio.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponseFilter{

  void filter(FullHttpResponse response);
}
