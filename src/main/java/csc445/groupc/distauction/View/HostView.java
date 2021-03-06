package csc445.groupc.distauction.View;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import csc445.groupc.distauction.HostServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class HostView {
    private JLabel ipAddress;
    private JLabel portNumber;
    public JPanel hostPanel;
    private JLabel numOfConnected;
    private JButton playBtn;
    private Optional<String> ip;
    private static int port = 9000;
    private ArrayList<String> usernames;
    private int id;
    private Thread server;
    private CountDownLatch startSignal;

    private final String gameInfoFile;

    public HostView(final String username, final String gameInfoFile) {
        playBtn.addActionListener(new BtnClicked());

        usernames = new ArrayList<>();
        this.gameInfoFile = gameInfoFile;

        try {
            ip = getIpAddress();
            ipAddress.setText(ip.orElse("Unable to get IP address"));
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

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to get the IPv4 IP address of the current machine.
     *
     * @return The IP address of the current machine.
     * @throws SocketException If it fails to get the machine's network interfaces.
     */
    private Optional<String> getIpAddress() throws SocketException {
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                final InetAddress addr = addresses.nextElement();

                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return Optional.of(addr.getHostAddress());
                }
            }
        }
        return Optional.empty();
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
        hostPanel = new JPanel();
        hostPanel.setLayout(new GridBagLayout());
        hostPanel.setPreferredSize(new Dimension(400, 300));
        ipAddress = new JLabel();
        Font ipAddressFont = this.$$$getFont$$$(null, -1, 18, ipAddress.getFont());
        if (ipAddressFont != null) ipAddress.setFont(ipAddressFont);
        ipAddress.setText("0.0.0.0");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        hostPanel.add(ipAddress, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 20, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Hosting");
        label1.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        hostPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, 18, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("IP:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        hostPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, -1, 18, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Port:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        hostPanel.add(label3, gbc);
        portNumber = new JLabel();
        Font portNumberFont = this.$$$getFont$$$(null, -1, 18, portNumber.getFont());
        if (portNumberFont != null) portNumber.setFont(portNumberFont);
        portNumber.setText("0000");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        hostPanel.add(portNumber, gbc);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 20, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        hostPanel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, -1, 18, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Connected Users:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        hostPanel.add(label5, gbc);
        numOfConnected = new JLabel();
        Font numOfConnectedFont = this.$$$getFont$$$(null, -1, 18, numOfConnected.getFont());
        if (numOfConnectedFont != null) numOfConnected.setFont(numOfConnectedFont);
        numOfConnected.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        hostPanel.add(numOfConnected, gbc);
        playBtn = new JButton();
        playBtn.setLabel("Start");
        playBtn.setMargin(new Insets(0, 2, 0, 2));
        playBtn.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        hostPanel.add(playBtn, gbc);
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

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return hostPanel;
    }

    private class BtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Play");
            server.interrupt();

            LoginView.frame.setContentPane(new GameView(usernames.toArray(new String[0]), id, HostServer.multicastAddr, gameInfoFile, Optional.empty()).mainPanel);
            LoginView.frame.pack();
            startSignal.countDown();
        }
    }

    public void addUser(String username) {
        usernames.add(username);
        numOfConnected.setText(Integer.toString(usernames.size()));
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    private void startServer() {

        System.out.println("Accepting connections on port: " + port);
        int nextNum = 1;
        startSignal = new CountDownLatch(1);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new HostServer(socket, nextNum++, startSignal, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
