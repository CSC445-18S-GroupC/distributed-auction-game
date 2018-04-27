package csc445.groupc.distauction.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinView {
    private JTextField hostIPField;
    private JTextField hostPortField;
    private JButton connectButton;
    public JPanel joinPanel;


    public JoinView(){
        connectButton.addActionListener(new BtnClicked());
    }

    private class BtnClicked implements ActionListener {
        String hostIP;
        String hostPort;

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(connectButton)) {
                this.hostIP = hostIPField.getText();
                this.hostPort = hostPortField.getText();
                System.out.println("IP: " + hostIP);
                System.out.println("Port: " + hostPort);
            }
        }
    }
}
