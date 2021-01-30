package com.shawn.study.nio.httpclient;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {

  private static final String URL = "http://localhost:8804";

  public static void main(String[] args) {
    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(URL);
      CloseableHttpResponse response = httpClient.execute(httpGet);
      if (response.getStatusLine().getStatusCode() == 200) {
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");
        System.out.println(result);
      } else {
        throw new RuntimeException("500");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
