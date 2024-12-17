package com.example;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLConnection;

public class MyThread extends Thread {

    BufferedReader in;
    DataOutputStream out;
    Socket socket;
    String header;
    String method;
    String resource;
    String version;
    String responseHeader;
    byte[] responseBody;
    int statusCode = 200;

    public MyThread(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            String firstLine = in.readLine();
            System.out.println(firstLine);
            String[] request = firstLine.split(" ");

            method = request[0];
            resource = request[1];
            version = request[2];

            do {
                header = in.readLine();
                System.out.println(header);
            } while (!header.isEmpty());
            System.out.println("Request ended");

            File file = getFile(resource);
            responseBody = getFileStream(file);

            webResponse(file);

            // closing resources
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile(String resource) {
        String basePath = "docs/progettoBootstrap";
        String rs = "";
        switch (resource) {
            case "/":
            case "/index":
            case "/index/":
                rs = "/index.html";
                break;

            case "/pages":
            case "/pages/":
                rs = "/pages/fnaf1.html";
                break;

            default:
                rs = resource;
        }
        return new File(
            basePath += rs
        );
    }

    public byte[] getFileStream(File file) throws IOException {
        System.out.println(file.getPath());
        if (file == null || !file.exists() || file.isDirectory()){
            statusCode = 404;
            return "<html><body><h1>404 Not Found</h1></body></html>".getBytes();
        } 

        InputStream input = new FileInputStream(file);
        byte[] buffer = new byte[200000];
        
        int bytesRead;
        ByteArrayOutputStream responseContent = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            responseContent.write(buffer, 0, bytesRead);
        }

        input.close();
        return responseContent.toByteArray();
    }

    public String getContentType(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return "unknown";
        }

        return URLConnection.guessContentTypeFromName(file.getName());
    }

    public void sendRedirect(String location) throws IOException {
        String redirectResponse = "HTTP/1.1 301 Moved Permanently\r\n" +  // 301 per reindirizzamento permanente
                                  "Location: " + location + "\r\n" +
                                  "Content-Length: 0\r\n" +
                                  "\r\n";
        out.writeBytes(redirectResponse);
        socket.close();
    }

    public void webResponse(File file) throws IOException{
        switch (statusCode) {
            case 200:
                out.writeBytes("HTTP/1.1 "+ statusCode + " OK " + System.lineSeparator());
                out.writeBytes("Content-Type: " + getContentType(file) + System.lineSeparator());
                out.writeBytes("Content-Length: " + responseBody.length + System.lineSeparator());
                out.writeBytes(System.lineSeparator());
                out.write(responseBody);
                break;
            
            case 301:
                out.writeBytes("HTTP/1.1 "+ statusCode + " Moved Permanently " + System.lineSeparator());
                out.writeBytes("Content-Type: " + getContentType(file) + System.lineSeparator());
                out.writeBytes("Location: " + file.getPath() + System.lineSeparator());
                out.writeBytes("Content-Length: " + responseBody.length + System.lineSeparator());
                out.writeBytes(System.lineSeparator());
                out.write(responseBody);
                break;

            case 404:
                out.writeBytes("HTTP/1.1 "+ statusCode + " Not Found " + System.lineSeparator());
                out.writeBytes("Content-Type: " + getContentType(file) + System.lineSeparator());
                out.writeBytes("Content-Length: " + responseBody.length + System.lineSeparator());
                out.writeBytes(System.lineSeparator());
                out.write(responseBody);
                break;

            default:
                out.writeBytes("HTTP/1.1 "+ responseHeader + System.lineSeparator());
                out.writeBytes("Content-Type: " + getContentType(file) + System.lineSeparator());
                out.writeBytes("Content-Length: " + responseBody.length + System.lineSeparator());
                out.writeBytes(System.lineSeparator());
                out.write(responseBody);
                break;
        }
    }
}
