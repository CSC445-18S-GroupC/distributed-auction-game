package csc445.groupc.distauction.View;

import csc445.groupc.distauction.HostClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinView {
    private JTextField hostIPField;
    private JTextField hostPortField;
    private JButton connectButton;
    public JPanel joinPanel;
    private String username;


    public JoinView(String username){
        connectButton.addActionListener(new BtnClicked());
        this.username = username;
    }

    private class BtnClicked implements ActionListener {
        String hostIP;
        int hostPort;

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(connectButton)) {
                this.hostIP = hostIPField.getText();
                this.hostPort = Integer.parseInt(hostPortField.getText());
                System.out.println("IP: " + hostIP);
                System.out.println("Port: " + hostPort);

                HostClient clientConnection = new HostClient(hostIP, hostPort);
                clientConnection.join(username);

                LoginView.frame.setContentPane(new GameView(clientConnection.getUsers(), clientConnection.getUsers().indexOf(username)).mainPanel);
                LoginView.frame.pack();
            }
        }
    }
}
