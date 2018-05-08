package csc445.groupc.distauction.View;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import csc445.groupc.distauction.Communication.MessageReceiving;
import csc445.groupc.distauction.Communication.MessageSending;
import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState;
import csc445.groupc.distauction.GameLogic.GameStep;
import csc445.groupc.distauction.GameLogic.Timeout;
import csc445.groupc.distauction.Paxos.Paxos;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class GameView {
    private JLabel bidLabel;
    private JButton bidButton;
    private JPanel playersPanel;
    public JPanel mainPanel;
    private GameState gameState;

    private final Phaser phaser;
    private final ReentrantLock gameInfoFileLock;

    public GameView(String[] usernames, int id, String multicastAddr, final String gameInfoFile,
                    final Optional<Collection<GameStep>> previousSteps) {
        phaser = new Phaser(1);
        gameInfoFileLock = new ReentrantLock();

        if (!previousSteps.isPresent()) {
            appendLineToFile(gameInfoFile, playersToString(usernames));
            appendLineToFile(gameInfoFile, id + "");
            appendLineToFile(gameInfoFile, multicastAddr);
            appendLineToFile(gameInfoFile, "--------");
        }

        final AtomicInteger prevGameRound = new AtomicInteger(1);
        final AtomicBoolean startedRun = new AtomicBoolean(false);
        gameState = new GameState(LocalDateTime.now(), usernames, (symbol) -> {
            System.out.println(gameState);
            updateUsers(gameState.getPlayerScores());
            updateBid(gameState.getMostRecentBid());

            if (startedRun.get()) {
                gameInfoFileLock.lock();
                try {
                    appendLineToFile(gameInfoFile, symbol.toString());
                } finally {
                    gameInfoFileLock.unlock();
                }
            }

            if (gameState.getRound() > prevGameRound.get()) {
                prevGameRound.set(gameState.getRound());
                phaser.arrive();
            }
        });

        updateUsers(gameState.getPlayerScores());

        Paxos<GameStep> paxos = new Paxos<>(id, usernames.length, (s) -> {
            gameState.applyStep(s);
        }, previousSteps);

        startedRun.set(true);
        paxos.run();

        onThread(() -> {
            try {
                MessageReceiving.run(multicastAddr, 5353, paxos.getReceivingQueue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        onThread(() -> {
            try {
                MessageSending.run(multicastAddr, 5354, 5353, paxos.getSendingQueue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bid Pressed");
                try {
                    paxos.proposeStep(gameState.generateRandomBid(usernames[id]));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        onThread(() -> {
            int waitingRound;
            while (true) {
                waitingRound = gameState.getRound();
                try {
                    phaser.awaitAdvanceInterruptibly(phaser.getPhase(), GameState.TIMEOUT_LENGTH, GameState.TIMEOUT_UNIT);
                } catch (Exception ex) {
                } finally {
                    try {
                        if (gameState.getRound() <= waitingRound) {
                            paxos.proposeStep(new Timeout(waitingRound));
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private static String playersToString(final String[] usernames) {
        return String.join(",", usernames);
    }

    private static void appendLineToFile(final String filePath, final String line) {
        try (final FileWriter fileWriter = new FileWriter(filePath, true);
             final BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.append(line + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateUsers(HashMap<String, Integer> playerScores) {
        playersPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        //c.insets = new Insets(10,0,0,10);
        String[] usernames = playerScores.keySet().toArray(new String[0]);
        int x = 0;
        int y = 0;
        for (int i = 0; i < playerScores.size(); i++) {
            JLabel bidLabel = new JLabel();
            //bidLabel.setText("username($0)");
            bidLabel.setText(usernames[i] + "($" + playerScores.get(usernames[i]) + ")");
            bidLabel.setFont(bidLabel.getFont().deriveFont(18.0f));

            c.gridx = x;
            c.gridy = y;
            playersPanel.add(bidLabel, c);

            x++;
            if (x == 3) {
                y++;
                x = 0;
            }
        }
    }

    public void updateBid(Optional<Bid> bid) {
        if (bid.isPresent()) {
            bidLabel.setText(bid.get().getBidder() + " ($" + bid.get().getBidAmount() + ")");
        } else {
            bidLabel.setText("$0");
        }

    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setMinimumSize(new Dimension(400, 300));
        mainPanel.setPreferredSize(new Dimension(450, 300));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Current Bid:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.ipady = 40;
        mainPanel.add(label1, gbc);
        bidLabel = new JLabel();
        Font bidLabelFont = this.$$$getFont$$$(null, -1, 20, bidLabel.getFont());
        if (bidLabelFont != null) bidLabel.setFont(bidLabelFont);
        bidLabel.setHorizontalAlignment(0);
        bidLabel.setHorizontalTextPosition(0);
        bidLabel.setText("username: $0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.ipady = 40;
        mainPanel.add(bidLabel, gbc);
        bidButton = new JButton();
        Font bidButtonFont = this.$$$getFont$$$(null, -1, 20, bidButton.getFont());
        if (bidButtonFont != null) bidButton.setFont(bidButtonFont);
        bidButton.setLabel("Bid");
        bidButton.setText("Bid");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(bidButton, gbc);
        playersPanel = new JPanel();
        playersPanel.setLayout(new GridBagLayout());
        Font playersPanelFont = this.$$$getFont$$$(null, -1, 20, playersPanel.getFont());
        if (playersPanelFont != null) playersPanel.setFont(playersPanelFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(playersPanel, gbc);
        playersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4521979)), null, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
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
