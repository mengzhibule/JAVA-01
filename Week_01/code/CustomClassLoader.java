package code;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * 自定义一个ClassLoader，加载一个Hello.xlass文件，执行hello方法。是通过这个小工具得到的，
 * java -jar fileconvert.jar Hello.class -> Hello.class.fck改名成 Hello.xlass
 */
public class CustomClassLoader extends ClassLoader {

  public static void main(String[] args) throws Exception {
    Object test = new CustomClassLoader().findClass("code.Test").newInstance();
    Class<?> clazz = test.getClass();
    Method method = clazz.getDeclaredMethod("testClassLoader");
    method.invoke(test);
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    byte[] bytes = new byte[0];
    try {
      bytes = inputStream2Byte();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return defineClass(name, bytes, 0, bytes.length);
  }

  private byte[] inputStream2Byte() throws IOException {
    InputStream inputStream =
        new FileInputStream(
            System.getProperty("user.dir")
                + File.separator
                + "Week_01"
                + File.separator
                + "code"
                + File.separator
                + "Test.xlass");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int byteValue;
    while ((byteValue = inputStream.read()) != -1) {
      outputStream.write(255 - byteValue);
    }
    return outputStream.toByteArray();
  }
}
