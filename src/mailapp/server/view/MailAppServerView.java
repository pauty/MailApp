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
    private JButton startButton;
    private JScrollPane logScrollPane;
    private JPanel buttonsPanel;
    private JTextArea logTextArea;
    
    private MailServerImpl server = null;
    private boolean serverShutDown = true;
    
    //CONTROLLER
    
    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Start")){
                boolean started = server.start();
                if(started){
                    startButton.setEnabled(false);
                    serverShutDown = false;
                }
            }
            else if(ae.getActionCommand().equals("Exit")){
                if(serverShutDown){
                    dispose(); 
                }
                else if(server != null){
                    serverShutDown = server.shutdown();
                    if(serverShutDown)
                        startButton.setEnabled(true);
                }
            }
        }
    }
    
    //END CONTROLLER
    
    public MailAppServerView(MailServerImpl s) {
        logScrollPane = new JScrollPane();
        buttonsPanel = new JPanel();
        logTextArea = new JTextArea(" \n");
        startButton = new JButton("Start");
        exitButton = new JButton("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        logTextArea.setEditable(false);
        logTextArea.setMargin(new Insets(20, 20, 20, 20));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setFont(new Font("Noto Sans", 0, 14)); 
        logScrollPane.setViewportView(logTextArea);

        getContentPane().add(logScrollPane, java.awt.BorderLayout.CENTER);
        
        exitButton.setFont(new Font("Noto Sans", 1, 18));
        exitButton.addActionListener(new ButtonListener());
        startButton.setFont(new Font("Noto Sans", 1, 18));
        startButton.addActionListener(new ButtonListener());
        buttonsPanel.add(startButton);
        buttonsPanel.add(exitButton);
        getContentPane().add(buttonsPanel, BorderLayout.PAGE_END);

        this.setSize(700,400);
        this.setTitle("MailApp Server");
        
        //set server
        server = s;
        server.addLogObserver(this);
        
    }
    
    //handle the log updates
    @Override
    public void update(Observable o, Object o1) {
        String logLine = (String)o1;
        logTextArea.append(logLine);
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }
                 
}