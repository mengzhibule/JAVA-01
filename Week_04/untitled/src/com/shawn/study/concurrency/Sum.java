package com.shawn.study.concurrency;

public class Sum {

  private Sum(){

  }

  public static int sum(int n){
    int sum = 0;
    for (int i = 0; i < n; i++) {
      sum += n;
    }
    return sum;
  }

}
