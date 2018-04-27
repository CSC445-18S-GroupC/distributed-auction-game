package csc445.groupc.distauction.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView {
    private JLabel bidLabel;
    private JButton bidButton;
    private JPanel playersPanel;
    private JPanel mainPanel;

    public GameView() {
        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        //c.insets = new Insets(10,0,0,10);
        int x = 0;
        int y = 0;
        for(int i =0; i < 7; i ++){
            JLabel bidLabel = new JLabel();
            bidLabel.setText("username($0)");
            bidLabel.setFont(bidLabel.getFont().deriveFont(18.0f));

            c.gridx = x;
            c.gridy = y;
            playersPanel.add(bidLabel, c);

            x++;
            if(x == 3){
                y++;
                x = 0;
            }
        }

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("BidGame");
        frame.setPreferredSize(new Dimension(450,300));
        frame.setContentPane(new GameView().mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
