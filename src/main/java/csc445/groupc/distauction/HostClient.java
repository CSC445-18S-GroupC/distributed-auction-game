package csc445.groupc.distauction;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HostClient {
    private String host;
    private int port;
    private String username;
    private String multicastAddr;
    private ArrayList<String> users;

    public HostClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void join(String username){
        this.username = username;
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            //send the username
            out.println(username);

            //receive multicast address
            multicastAddr = (String)inputStream.readObject();
            System.out.println("Received multicast address: " + multicastAddr);

            //wait and receive all usernames
            users = (ArrayList<String>) inputStream.readObject();
            System.out.println(users.size() + users.get(0));


        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getMulticastAddr() {
        return multicastAddr;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public static void main(String[] args) {
        HostClient client = new HostClient("localhost", 9000);
        client.join("username");
    }
}
