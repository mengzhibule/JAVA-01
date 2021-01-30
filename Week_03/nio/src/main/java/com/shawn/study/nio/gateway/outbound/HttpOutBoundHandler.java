package com.shawn.study.nio.gateway.outbound;

import com.shawn.study.nio.gateway.filter.HttpRequestFilter;
import com.shawn.study.nio.gateway.router.RandomHttpEndpointRouter;
import com.shawn.study.nio.gateway.router.Router;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpOutBoundHandler {
  private CloseableHttpClient httpclient = HttpClients.createDefault();
  private List<String> backendUrls;

  private ExecutorService proxyService;
  /** 路由转发 */
  Router router;

  public HttpOutBoundHandler(List<String> backServerUrls) {
    router = new RandomHttpEndpointRouter();
    this.backendUrls = backServerUrls;
    int cores = Runtime.getRuntime().availableProcessors();
    long keepAliveTime = 1000;
    int queueSize = 2048;
    RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
    proxyService =
        new ThreadPoolExecutor(
            cores,
            cores,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueSize),
            new NamedThreadFactory("proxyService"),
            handler);
  }

  public void handle(
      final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx,
      HttpRequestFilter filter) {
    String backendUrl = router.route(this.backendUrls);
    final String url = backendUrl + fullRequest.uri();
    filter.filter(fullRequest, ctx);
    proxyService.submit(() -> getAsString(url));
  }

  // GET 调用
  public String getAsString(String url) throws IOException {
    HttpGet httpGet = new HttpGet(url);
    CloseableHttpResponse response1 = null;
    try {
      response1 = httpclient.execute(httpGet);
      System.out.println(response1.getStatusLine());
      HttpEntity entity1 = response1.getEntity();
      return EntityUtils.toString(entity1, "UTF-8");
    } finally {
      if (null != response1) {
        response1.close();
      }
    }
  }
}
