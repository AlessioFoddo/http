package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        ServerSocket ss = new ServerSocket(3000);

        do{
            Socket s = ss.accept();
            System.out.println("Un client si è collegato!");
            MyThread t = new MyThread(s);
            t.start();
        }while (true);
    }
}