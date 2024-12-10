package com.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

public class MyThread extends Thread {
    
    private Socket s;
    BufferedReader in;
    DataOutputStream out;
    String s1;
    String method;
    String resource;
    String version;

    public MyThread(Socket s) throws IOException{
        this.s = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new DataOutputStream(s.getOutputStream());
    }

    public void run(){
        try {

            String firstLine = in.readLine();
            System.out.println(firstLine);
            String[] lines = firstLine.split(" ");

            method = lines[0];
            resource = lines[1];
            version = lines[2];

            do{
                s1 = in.readLine();
                System.out.println(s1);
            }while(!s1.isEmpty());
            System.out.println("ended");

            String response = "";
            String head = "";
            String code = "";
            int length = 0;
            String type = "";
            
            String end = "";

            switch (resource) {
                case "/":
                case "/index.html":
                    response = "<h1>Ciaoooooooo, Loda Kurumi, mi raccommando</h1>\n";
                    code = "200 OK";
                    length = response.length();
                    type = "text/html";
                    break;

                case "/file.txt":
                    response = "Ecco un testo random\n";
                    code = "200 OK";
                    length = response.length();
                    type = "text/plain";
                    break;

                case "/kurumi":
                case "/kurumi.html":
                    File file = new File("src/docs/index.html");
                    InputStream input = new FileInputStream(file);
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = input.read(buf)) != -1) {
                        response += new String(buf, 0, n);
                    }
                    break;

                default:
                    response = "<h1>404 NOT FOUND</h1> </br> <p>cerca da un'altra parte, coglione</p>\n";
                    code = "404 not found";
                    length = response.length();
                    type = "text/plain";
                    break;
                }
                head = "HTTP/1.1 " + code + "\n" +
                        "Cotent-Type: " + type + "\n" +
                        "Cotent-Length: " + length + "\n" +
                        "\n";
                end = head + response;
                out.writeBytes(end);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
