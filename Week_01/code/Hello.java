package code;

import java.util.Scanner;

/** 演示基本数据类型的字节码 演示四则运算的字节码 演示循环，流程，分支等字节码 */
public class Hello {

  public static void main(String[] args) {
    testBasicType();
    testArithmetic();
    testLoop();
  }

  /** 演示基本数据类型的字节码 */
  public static void testBasicType() {
    byte b = 1;
    short s = 2;
    int i = 3;
    long l = 4L;
    float f = 5.5F;
    double d = 6.6D;
    char c = 'A';
  }

  /** 演示四则运算 */
  public static void testArithmetic() {
    byte a = 1;
    byte b = 2;
    byte c = (byte) (a + b);

    short d = 4;
    short e = 5;
    short f = (short) (d * e);

    int i1 = 10;
    int i2 = 5;
    int i3 = i1 / i2;

    long l1 = 50;
    long l2 = 30;
    long l3 = l1 - l2;
  }

  /** 演示循环 */
  public static void testLoop() {
    String[] arr = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};

    System.out.println("演示for循环");
    for (int i = 0; i < arr.length; i++) {
      System.out.println(arr[i]);
    }

    System.out.println("演示forEach循环");
    for (String i : arr) {
      System.out.println(i);
    }

    System.out.println("演示while循环");
    int i = 0;
    while (i < arr.length) {
      System.out.println(arr[i]);
      i++;
    }

    System.out.println("演示do-while循环");
    int index = 0;

    do {
      System.out.println(arr[index]);
      index++;
    } while (index >= arr.length);

    Scanner scanner = new Scanner(System.in);
    String next = scanner.next();
    System.out.println("演示if-else");
    if ("1".equals(next)) {
      System.out.println("run case one");
    } else if ("2".equals(next)) {
      System.out.println("run case two");
    } else if ("3".equals(next)) {
      System.out.println("run case three");
    } else {
      System.out.println("run default case");
    }

    System.out.println("演示switch-case");
    switch (next) {
      case "1":
        System.out.println("run case one");
        break;
      case "2":
        System.out.println("run case two");
        break;
      case "3":
        System.out.println("run case three");
        break;
      default:
        System.out.println("run default case");
        break;
    }

    System.out.println("演示跳转语句");
    for (int j = 0; j < arr.length; j++) {
      if ("break".equals(next)) {
        System.out.println("演示break");
        break;
      }
      if ("continue".equals(next)) {
        System.out.println("演示continue");
        continue;
      }
      if (next.trim().length() == 0) {
        System.out.println("演示return");
        return;
      }
      if (next.equals(arr[i])) {
        System.out.println(arr[1]);
      }
    }
  }
}
