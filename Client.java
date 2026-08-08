import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame {

    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    // Networking
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {

        setTitle("Secure Chat Client");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        connectServer();

        startReading();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

    }

    // Connect with Server
    private void connectServer() {

        try {

            socket = new Socket("localhost", 24);

            out = new PrintWriter(socket.getOutputStream(), true);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            chatArea.append("Connected to Server...\n");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Unable to connect to Server");

        }

    }    // Send Message
    private void sendMessage() {

        try {

            String message = messageField.getText().trim();

            if (message.isEmpty()) {
                return;
            }

            // Show own message
            chatArea.append("Me : " + message + "\n");

            // Encrypt message
            String encryptedMessage = AESUtil.encrypt(message);

            // Send encrypted message
            out.println(encryptedMessage);

            // Clear text field
            messageField.setText("");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Failed to send message!");

            e.printStackTrace();

        }

    }

    // Receive Messages
    private void startReading() {

        Thread readerThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    String encryptedMessage;

                    while ((encryptedMessage = in.readLine()) != null) {

                        String decryptedMessage =
                                AESUtil.decrypt(encryptedMessage);

                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                chatArea.append("Server : "
                                        + decryptedMessage + "\n");

                            }

                        });

                    }

                } catch (Exception e) {

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            chatArea.append(
                                    "Connection Closed.\n");

                        }

                    });

                }

            }

        });

        readerThread.start();

    }    // Close Resources
    private void closeConnection() {

        try {

            if (out != null)
                out.close();

            if (in != null)
                in.close();

            if (socket != null)
                socket.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // Main Method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                Client client = new Client();

                client.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {

                        client.closeConnection();

                    }

                });

            }

        });

    }

}
