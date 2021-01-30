package com.shawn.study.nio.httpclient;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.nio.charset.StandardCharsets;

public class NettyHttpServer {

  public static void main(String[] args) {
    start();
  }

  private static void start() {
    int port = 8804;
    EventLoopGroup bossGroup = new NioEventLoopGroup(2);
    EventLoopGroup workerGroup = new NioEventLoopGroup(16);
    ServerBootstrap bootstrap = new ServerBootstrap();
    try {
      bootstrap
          .option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.SO_REUSEADDR, true)
          .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
          .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
          .childOption(EpollChannelOption.SO_REUSEPORT, true)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(
              (new ChannelInitializer<SocketChannel>() {
                // 绑定客户端时触发的操作
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                  ChannelPipeline pipeline = socketChannel.pipeline();
                  pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                  pipeline.addLast(new NettySocketServerHandler()); // 服务器处理客户端请求
                  pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
                }
              }));

      Channel ch = bootstrap.bind(port).sync().channel();
      System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
      ch.closeFuture().sync();
    } catch (Exception e) {
      throw new RuntimeException("500");
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
