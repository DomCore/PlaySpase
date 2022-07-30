package com.example.templates;

public class te {
  public static void main(String[] args) {
    String a = "1010.1";

    String mainChapterNum = a.substring(0, a.indexOf("."));
    System.out.println(mainChapterNum);
  }
}
