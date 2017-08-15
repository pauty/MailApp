package mailapp.server.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import mailapp.server.MailServerImpl;

/**
 *
 * @author pauty
 */
public class MailAppServerView extends JFrame implements Observer{
    private JButton exitButton;
    private JScrollPane logScrollPane;
    private JTextArea logTextArea;
    private MailServerImpl server = null;
    
    //CONTROLLER
    
    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(server != null){
                server.shutdown();     
            }
            dispose();
        }
    }
    
    //END CONTROLLER
    
    public MailAppServerView(MailServerImpl s) {
        logScrollPane = new JScrollPane();
        logTextArea = new JTextArea("server set up-------------\n\n\n");
        exitButton = new JButton("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Noto Sans", 0, 14)); 
        logScrollPane.setViewportView(logTextArea);

        getContentPane().add(logScrollPane, java.awt.BorderLayout.CENTER);
        
        exitButton.setFont(new Font("Noto Sans", 1, 16));
        exitButton.addActionListener(new ButtonListener());
        getContentPane().add(exitButton, java.awt.BorderLayout.PAGE_END);

        this.setSize(700,400);
        
        //set server
        server = s;
        server.addLogObserver(this);
        
    }
    
    //handle the log updates
    @Override
    public void update(Observable o, Object o1) {
        String logLine = (String)o1;
        logTextArea.append(logLine);
    }
                 
}