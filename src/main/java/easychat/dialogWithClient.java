package easychat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class dialogWithClient extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField input;
    private JTextArea message;

    client_window client;

    public dialogWithClient(client_window client) {
        this.client = client;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String ipHost = input.getText();
                    if ("1".equals(ipHost)) {
                        ipHost = "localhost";
                        System.out.println("ip = localhost");
                    } else if ("2".equals(ipHost)) {
                        ipHost = "192.168.1.248";
                        System.out.println("ip = 192.168.1.248");
                    }
                    try {
                        new ChatClient(ipHost, 8000).run();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    private void onOK() {
        String ipHost = input.getText();
        dispose();
        if ("1".equals(ipHost)) {
            ipHost = "localhost";
            System.out.println("ip = localhost");
        } else if ("2".equals(ipHost)) {
            ipHost = "192.168.1.248";
            System.out.println("ip = 192.168.1.248");
        }
        client.setAddress(ipHost);
        // add your code here
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void run(client_window client){
        dialogWithClient dialog = new dialogWithClient(client);
        dialog.pack();
        dialog.setVisible(true);
    }
    public static void main(String[] args) {
    }

}
