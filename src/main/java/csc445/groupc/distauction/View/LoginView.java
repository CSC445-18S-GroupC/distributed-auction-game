package csc445.groupc.distauction.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView {
    public static JFrame frame;
    private JPanel loginPanel;
    private JButton hostButton;
    private JButton joinButton;
    private JTextField usernameField;

    public LoginView(){
        hostButton.addActionListener(new BtnClicked());
        joinButton.addActionListener(new BtnClicked());
    }

    private class BtnClicked implements ActionListener {
        String username;

        @Override
        public void actionPerformed(ActionEvent e) {
            this.username = usernameField.getText();

            if(e.getSource().equals(hostButton)){
                System.out.println("Host Button");

                //switch panel to host
                frame.setContentPane(new HostView(username).hostPanel);
                frame.pack();
            }else if (e.getSource().equals(joinButton)){
                System.out.println("Join Button");

                //switch panel to join
                frame.setContentPane(new JoinView(username).joinPanel);
                frame.pack();
            }
            System.out.println("Username: " + username);
        }
    }



    public static void main() {
        frame = new JFrame("BidGame");
        frame.setContentPane(new LoginView().loginPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
