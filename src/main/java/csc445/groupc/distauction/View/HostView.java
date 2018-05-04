package csc445.groupc.distauction.View;

import csc445.groupc.distauction.HostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class HostView {
    private JLabel ipAddress;
    private JLabel portNumber;
    public JPanel hostPanel;
    private JLabel numOfConnected;
    private JButton playBtn;
    private String ip;
    private static int port = 9000;
    private ArrayList<String> usernames;
    private int id;
    private Thread server;
    private CountDownLatch startSignal;

    public HostView(String username){
        playBtn.addActionListener(new BtnClicked());

        usernames = new ArrayList<>();

        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
            ipAddress.setText(ip);
            portNumber.setText(Integer.toString(port));

            server = new Thread(new Runnable() {

                @Override
                public void run() {
                    startServer();
                }

            });

            server.start();
            addUser(username);
            id = usernames.indexOf(username);

        } catch (UnknownHostException e){
            e.printStackTrace();
        }
    }


    private class BtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Play");
            server.interrupt();

            LoginView.frame.setContentPane(new GameView(usernames, id).mainPanel);
            LoginView.frame.pack();
            startSignal.countDown();
        }
    }

    public void addUser(String username){
        usernames.add(username);
        numOfConnected.setText(Integer.toString(usernames.size()));
    }

    public ArrayList<String> getUsernames(){
        return usernames;
    }

    private void startServer(){

        System.out.println("Accepting connections on port: " + port);
        int nextNum = 1;
        startSignal = new CountDownLatch(1);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new HostServer(socket, nextNum++, startSignal, this);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
