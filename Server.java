import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Server extends JFrame {

    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    // Networking
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Server() {

        setTitle("Secure Chat Server");
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

        startServer();

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

    // Start Server
    private void startServer() {

        try {

            serverSocket = new ServerSocket(24);

            chatArea.append("Server Started...\n");
            chatArea.append("Waiting for Client...\n");

            socket = serverSocket.accept();

            chatArea.append("Client Connected.\n");

            out = new PrintWriter(socket.getOutputStream(), true);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Server Error!");

            e.printStackTrace();

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

            // Send encrypted message to Client
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

                        // Decrypt received message
                        String decryptedMessage =
                                AESUtil.decrypt(encryptedMessage);

                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                chatArea.append("Client : "
                                        + decryptedMessage + "\n");

                            }

                        });

                    }

                } catch (Exception e) {

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            chatArea.append("Connection Closed.\n");

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

            if (serverSocket != null)
                serverSocket.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // Main Method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                Server server = new Server();

                server.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {

                        server.closeConnection();

                    }

                });

            }

        });

    }

}
