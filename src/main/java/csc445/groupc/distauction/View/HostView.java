package csc445.groupc.distauction.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class HostView {
    private JLabel ipAddress;
    private JLabel portNumber;
    public JPanel hostPanel;
    private JLabel numOfConnected;
    private JButton playBtn;

    public HostView(){
        playBtn.addActionListener(new BtnClicked());
        try {
            ipAddress.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
    }


    private class BtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Play");
        }
    }

}
