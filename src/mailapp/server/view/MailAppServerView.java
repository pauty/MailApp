package mailapp.server.view;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author pauty
 */
public class MailAppServerView extends JFrame {
    private JButton exitButton;
    private JScrollPane logScrollPane;
    private JTextArea logTextArea;
    
    public MailAppServerView() {
        logScrollPane = new JScrollPane();
        logTextArea = new JTextArea("server set up-------------\n\n\n");
        exitButton = new JButton("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Noto Sans", 0, 14)); 
        logScrollPane.setViewportView(logTextArea);

        getContentPane().add(logScrollPane, java.awt.BorderLayout.CENTER);
        
        exitButton.setFont(new Font("Noto Sans", 1, 16));
        getContentPane().add(exitButton, java.awt.BorderLayout.PAGE_END);

        this.setSize(700,400);
        
    }
                 
}