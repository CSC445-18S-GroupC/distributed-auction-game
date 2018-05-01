package csc445.groupc.distauction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class HostServer implements Runnable {
    private final Socket m_socket;
    private final int m_num;
    private final CountDownLatch startSignal;
    private static final String multicastAddr = "230.1.1.1";
    private static ArrayList<String> usernames;

    HostServer(Socket socket, int num, CountDownLatch startSignal) {
        this.m_socket = socket;
        this.m_num = num;
        this.startSignal = startSignal;

        Thread handler = new Thread(this, "handler-" + m_num);
        handler.start();
    }

    public void run() {
        try {
            try {
                System.out.println(m_num + " Connected.");
                BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                ObjectOutputStream outputStream = new ObjectOutputStream(m_socket.getOutputStream());

                //get username from client
                String username = in.readLine();
                System.out.println(username + " connected");

                usernames.add(username);

                //Send back multicast address
                outputStream.writeObject(multicastAddr);

                try {
                    //wait for everyone to connect
                    startSignal.await();
                }catch (InterruptedException e ){
                    e.printStackTrace();
                }

                //send out all usernames
                outputStream.writeObject(usernames);


            } finally {
                m_socket.close();
            }
        } catch (IOException e) {
            System.out.println(m_num + " Error: " + e.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 9000;
        usernames = new ArrayList<>();
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Accepting connections on port: " + port);
        int nextNum = 1;
        CountDownLatch startSignal = new CountDownLatch(1);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            new HostServer(socket, nextNum++, startSignal);
        }
    }
}