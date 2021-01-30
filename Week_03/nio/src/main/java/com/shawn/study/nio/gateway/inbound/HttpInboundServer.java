package com.shawn.study.nio.gateway.inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.List;

public class HttpInboundServer {
  private int port;

  private List<String> proxyServers;

  public HttpInboundServer(int port, List<String> proxyServers) {
    this.port=port;
    this.proxyServers = proxyServers;
  }

  public void run() throws Exception {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(16);

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.SO_REUSEADDR, true)
          .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
          .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
          .childOption(EpollChannelOption.SO_REUSEPORT, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.DEBUG))
          .childHandler(new HttpInboundInitializer(this.proxyServers));

      Channel ch = b.bind(port).sync().channel();
      System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
      ch.closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

  private List<String> proxyServer;

  public HttpInboundInitializer(List<String> proxyServer) {
    this.proxyServer = proxyServer;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();

    p.addLast(new HttpServerCodec());
    //p.addLast(new HttpServerExpectContinueHandler());
    p.addLast(new HttpObjectAggregator(1024 * 1024));
    p.addLast(new HttpInboundHandler(this.proxyServer));
  }
}



