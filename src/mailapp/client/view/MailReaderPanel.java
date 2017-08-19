/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.view;

import mailapp.EMail;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import mailapp.User;

public class MailReaderPanel extends javax.swing.JPanel {
                   
    private JLabel actualDateLabel;
    private JLabel actualFromLabel;
    private JPanel actualLabelsPanel;
    private JLabel actualSubjectLabel;
    private JLabel actualToLabel;
    private JButton backButton;
    private JPanel buttonsPanel;
    private JLabel dateLabel;
    private JPanel formalLabelsPanel;
    private JButton forwardButton;
    private JLabel fromLabel;
    private JPanel messagePanel;
    private JScrollPane messageScrollPane;
    private JTextArea messageTextArea;
    private JButton replyButton;
    private JButton replyallButton;
    private JLabel subjectLabel;
    private JLabel toLabel;
    private JPanel topPanel;
    
    private EMail readingMail; 
    private MailAppClientView parentFrame;
    
    //CONTROLLER
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Back")){
                parentFrame.showInboxPanel();
            }
            else if(ae.getActionCommand().equals("Forward")){
                parentFrame.showMailWriterPanel(readingMail, EMail.Type.FORWARD);
            }
            else if(ae.getActionCommand().equals("Reply")){
                parentFrame.showMailWriterPanel(readingMail, EMail.Type.REPLY);
            }
            else if(ae.getActionCommand().equals("Reply-All")){
                parentFrame.showMailWriterPanel(readingMail, EMail.Type.REPLY_ALL);
            }
        }
        
    }
    
    //END CONTROLLER

    public MailReaderPanel(MailAppClientView parent) {
        //set parent
        parentFrame = parent;
        
        //init GUI
        topPanel = new JPanel();
        formalLabelsPanel = new JPanel();
        subjectLabel = new JLabel("Subject");
        fromLabel = new JLabel("From");
        toLabel = new JLabel("To");
        dateLabel = new JLabel("Date");
        buttonsPanel = new JPanel();
        backButton = new JButton("Back");
        forwardButton = new JButton("Forward");
        replyButton = new JButton("Reply");
        replyallButton = new JButton("Reply-All");
        actualLabelsPanel = new JPanel();
        actualSubjectLabel = new JLabel();
        actualFromLabel = new JLabel();
        actualToLabel = new JLabel();
        actualDateLabel = new JLabel();
        messagePanel = new JPanel();
        messageScrollPane = new JScrollPane();
        messageTextArea = new JTextArea();
        Font boldFont = new Font("Noto Sans", 1, 14);
        Font normalFont = new Font("Noto Sans", 0, 14);
        Font bigFont = new Font("Noto Sans", 1, 16);

        setLayout(new BorderLayout());

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout());

        formalLabelsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        formalLabelsPanel.setLayout(new GridLayout(4, 1));

        subjectLabel.setFont(boldFont);
        formalLabelsPanel.add(subjectLabel);

        fromLabel.setFont(boldFont); 
        formalLabelsPanel.add(fromLabel);

        toLabel.setFont(boldFont); 
        formalLabelsPanel.add(toLabel);

        dateLabel.setFont(boldFont); 
        formalLabelsPanel.add(dateLabel);

        topPanel.add(formalLabelsPanel, BorderLayout.WEST);

        ButtonsListener buttonsListener = new ButtonsListener();
        
        backButton.setFont(bigFont); 
        buttonsPanel.add(backButton);
        backButton.addActionListener(buttonsListener);

        forwardButton.setFont(bigFont); 
        buttonsPanel.add(forwardButton);
        forwardButton.addActionListener(buttonsListener);

        replyButton.setFont(bigFont); 
        buttonsPanel.add(replyButton);
        replyButton.addActionListener(buttonsListener);

        replyallButton.setFont(bigFont); 
        buttonsPanel.add(replyallButton);
        replyallButton.addActionListener(buttonsListener);

        topPanel.add(buttonsPanel, BorderLayout.EAST);

        actualLabelsPanel.setLayout(new GridLayout(4, 1));

        actualSubjectLabel.setFont(normalFont); 
        actualLabelsPanel.add(actualSubjectLabel);

        actualFromLabel.setFont(normalFont); 
        actualLabelsPanel.add(actualFromLabel);

        actualToLabel.setFont(normalFont); 
        actualLabelsPanel.add(actualToLabel);

        actualDateLabel.setFont(normalFont); 
        actualLabelsPanel.add(actualDateLabel);

        topPanel.add(actualLabelsPanel, BorderLayout.CENTER);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setDoubleBuffered(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));
        
        messageTextArea.setFont(normalFont);
        messageTextArea.setEditable(false);
        messageTextArea.setMargin(new Insets(20, 20, 20, 20));
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        
        messageScrollPane.setViewportView(messageTextArea);

        messagePanel.add(messageScrollPane);

        add(messagePanel, BorderLayout.CENTER);
    }
    
    public void showMail(EMail mail){
        readingMail = mail;
        
        actualSubjectLabel.setText("<html>" + mail.getSubject() + "</html>");
        
        actualFromLabel.setText(mail.getSender().getName() + " <"+mail.getSender().getAddress()+">");
        
        actualToLabel.setText("<html>" + User.printUserAddressesList(mail.getReceivers(), ", ") + "</html>");
        
        actualDateLabel.setText(mail.getDateString("dd/MM/yyyy   HH:mm:ss"));
        
        messageTextArea.setText(mail.getBody());
        
        messageTextArea.setCaretPosition(0);
    }
}
