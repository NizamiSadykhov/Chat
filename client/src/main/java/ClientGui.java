import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class ClientGui extends JFrame {
    private final JPanel mLoginPanel = new JPanel();
    private final JTextArea mTextArea = new JTextArea();
    private final JPanel mChatPanel = new JPanel();
    private final JScrollPane mJs = new JScrollPane(mTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JTextField mTextInput = new JTextField();
    private final JTextArea mClientsInformation = new JTextArea();
    private final JScrollPane mJsClients = new JScrollPane(mClientsInformation, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private final JLabel mTextInputLabel = new JLabel("Your message :");
    private final JButton mSendButton = new JButton("Send");
    private final ActionListener mListener = event -> {
        String message = mTextInput.getText();
        if (!message.isEmpty()){
            ClientGui.this.mClientNetwork.sendMessage(mTextInput.getText());
            mTextInput.setText("");
        }

    };
    private final ClientNetwork mClientNetwork;

    public ClientGui() {
        mClientNetwork = new ClientNetwork();
        setCallbacks();
        mClientNetwork.connect();

        setMainFrame();

        initializeLoginJPanel();
        initializeChatPanel();
        this.setVisible(true);
    }

    private void setMainFrame() {
        this.setTitle("Chat");
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new FlowLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mClientNetwork.sendMessage("/end");
                super.windowClosing(e);
            }
        });
    }

    private void initializeChatPanel() {
        mTextArea.setEditable(false);
        mClientsInformation.setEditable(false);
        mChatPanel.setBackground(Color.white);
        mChatPanel.setPreferredSize(new Dimension(490, 490));
        mJs.setPreferredSize(new Dimension(450, 350));
        mJsClients.setPreferredSize(new Dimension(450, 35));
        mChatPanel.add(mJsClients);
        mChatPanel.add(mJs);
        mTextInput.setPreferredSize(new Dimension(150, 25));
        mChatPanel.add(mTextInputLabel);
        mChatPanel.add(mTextInput);

        mTextInput.addActionListener(mListener);
        mSendButton.addActionListener(mListener);
        mChatPanel.add(mSendButton);
        mChatPanel.setVisible(false);
        this.add(mChatPanel);
    }

    private void initializeLoginJPanel() {
        mLoginPanel.setBackground(Color.white);
        mLoginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        mLoginPanel.setPreferredSize(new Dimension(300, 150));
        mLoginPanel.setBorder(BorderFactory.createTitledBorder("Authorization"));
        JTextField login = new JTextField();
        JLabel loginLabel = new JLabel("Your connection identifier : ");
        JLabel passwordLabel = new JLabel("Your password : ");
        JPasswordField password = new JPasswordField();
        login.setPreferredSize(new Dimension(100, 25));
        password.setPreferredSize(new Dimension(100, 25));
        mLoginPanel.add(loginLabel);
        mLoginPanel.add(login);
        mLoginPanel.add(passwordLabel);
        mLoginPanel.add(password);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(280, 35));
        buttonPanel.setBackground(Color.WHITE);
        JButton button = new JButton("Submit");
        buttonPanel.add(button);
        button.addActionListener(e -> { // при нажатии на кнопку submit серверу запрос на авторизацию
            mClientNetwork.sendMessage("/auth " + login.getText() + " " + String.valueOf(password.getPassword()));
            login.setText("");// спрашиваем данные логина и пароля
            password.setText("");
        });
        mLoginPanel.add(buttonPanel);
        mLoginPanel.setVisible(true);
        this.add(mLoginPanel);
    }

    private void setCallbacks() {
        mClientNetwork.setCallOnMsgReceived(message -> mTextArea.append(message + "\n"));
        mClientNetwork.setCallOnChangeClientList(clientsList -> mClientsInformation.setText(clientsList));
        mClientNetwork.setCallOnAuth(s -> {
            mLoginPanel.setVisible(false);
            mChatPanel.setVisible(true);
        });
        mClientNetwork.setCallOnError(message ->
                JOptionPane.showMessageDialog(null, message, "We have a problem!", JOptionPane.ERROR_MESSAGE));
    }
}
