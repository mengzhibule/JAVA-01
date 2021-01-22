import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class HttpServer {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8080);

    while (true) {
      try {
        Socket socket = serverSocket.accept();
        service(socket);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void service(Socket socket) {
    try {
      PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
      printWriter.println("HTTP/1.1 200 OK");
      printWriter.println("Content-Type:text/html;charset=utf-8");
      String body = "hello world !";
      printWriter.println("content-length:"+body.getBytes().length);
      printWriter.println();
      printWriter.write(body);
      printWriter.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
