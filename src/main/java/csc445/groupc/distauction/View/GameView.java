package csc445.groupc.distauction.View;

import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState;
import csc445.groupc.distauction.GameLogic.GameStep;
import csc445.groupc.distauction.Paxos.Paxos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class GameView {
    private JLabel bidLabel;
    private JButton bidButton;
    private JPanel playersPanel;
    public JPanel mainPanel;
    private GameState gameState;

    public GameView(ArrayList<String> usernames, int id) {
        String[] players = usernames.toArray(new String[0]);
        gameState = new GameState(LocalDateTime.now(), players, (gs) -> {
            System.out.println(gs);
            updateUsers(gs.getPlayerScores());
            updateBid(gs.getMostRecentBid());
        });
        updateUsers(gameState.getPlayerScores());

        Paxos<GameStep> paxos = new Paxos<>(id, usernames.size(), (s) -> {
            gameState.applyStep(s);
        });
        paxos.run();

        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bid Pressed");
                try {
                    paxos.proposeStep(gameState.generateRandomBid(usernames.get(id)));
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        });


    }

    public void updateUsers(HashMap<String, Integer> playerScores) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        //c.insets = new Insets(10,0,0,10);
        String[] usernames = playerScores.keySet().toArray(new String[0]);
        int x = 0;
        int y = 0;
        for(int i =0; i < playerScores.size(); i ++){
            JLabel bidLabel = new JLabel();
            //bidLabel.setText("username($0)");
            bidLabel.setText(usernames[i] + "($" + playerScores.get(usernames[i]) + ")");
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

    public void updateBid(Optional<Bid> bid){
        if(bid.isPresent()){
            bidLabel.setText(bid.get().getBidder() + " ($" + bid.get().getBidAmount() + ")");
        } else {
            bidLabel.setText("$0");
        }

    }


//    public static void main(String[] args) {
//        JFrame frame = new JFrame("BidGame");
//        frame.setPreferredSize(new Dimension(450,300));
//        frame.setContentPane(new GameView().mainPanel);
//        frame.setLocationRelativeTo(null);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//
//    }
}
