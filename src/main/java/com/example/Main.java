package com.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world! .:.:");

        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i<4; i++) a.add("Hello");
        for (String s : a)
        {
            System.out.println(s);
        }
    }
}