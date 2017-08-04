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
import mailapp.client.MailAppClient;

public class MailReaderPanel extends javax.swing.JPanel {
        // Variables declaration - do not modify                     
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
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Back")){
                MailAppClient.getInstance().showInboxPanel();
            }
            else if(ae.getActionCommand().equals("Forward")){
                MailAppClient.getInstance().showMailWriterPanel(readingMail, EMail.Type.FORWARD);
            }
            else if(ae.getActionCommand().equals("Reply")){
                MailAppClient.getInstance().showMailWriterPanel(readingMail, EMail.Type.REPLY);
            }
        }
        
    }

    public MailReaderPanel() {
        
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

        setLayout(new java.awt.BorderLayout());

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new java.awt.BorderLayout());

        formalLabelsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        formalLabelsPanel.setLayout(new GridLayout(4, 1));

        subjectLabel.setFont(new java.awt.Font("Noto Sans", 1, 14));
        formalLabelsPanel.add(subjectLabel);

        fromLabel.setFont(new java.awt.Font("Noto Sans", 1, 14)); 
        formalLabelsPanel.add(fromLabel);

        toLabel.setFont(new java.awt.Font("Noto Sans", 1, 14)); 
        formalLabelsPanel.add(toLabel);

        dateLabel.setFont(new java.awt.Font("Noto Sans", 1, 14)); 
        formalLabelsPanel.add(dateLabel);

        topPanel.add(formalLabelsPanel, BorderLayout.WEST);

        buttonsPanel.setLayout(new GridLayout());

        ButtonsListener buttonsListener = new ButtonsListener();
        
        backButton.setFont(new Font("Noto Sans", 1, 16)); 
        buttonsPanel.add(backButton);
        backButton.addActionListener(buttonsListener);

        forwardButton.setFont(new Font("Noto Sans", 1, 16)); 
        buttonsPanel.add(forwardButton);
        forwardButton.addActionListener(buttonsListener);

        replyButton.setFont(new Font("Noto Sans", 1, 16)); 
        buttonsPanel.add(replyButton);
        replyButton.addActionListener(buttonsListener);

        replyallButton.setFont(new Font("Noto Sans", 1, 16)); 
        buttonsPanel.add(replyallButton);
        replyallButton.addActionListener(buttonsListener);

        topPanel.add(buttonsPanel, BorderLayout.EAST);

        actualLabelsPanel.setLayout(new GridLayout(4, 1));

        actualSubjectLabel.setFont(new Font("Noto Sans", 0, 14)); 
        actualLabelsPanel.add(actualSubjectLabel);

        actualFromLabel.setFont(new Font("Noto Sans", 0, 14)); 
        actualLabelsPanel.add(actualFromLabel);

        actualToLabel.setFont(new Font("Noto Sans", 0, 14)); 
        actualLabelsPanel.add(actualToLabel);

        actualDateLabel.setFont(new Font("Noto Sans", 0, 14)); 
        actualLabelsPanel.add(actualDateLabel);

        topPanel.add(actualLabelsPanel, BorderLayout.CENTER);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setDoubleBuffered(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));

        messageTextArea.setEditable(false);
        messageTextArea.setColumns(20); ////////////////////////
        messageTextArea.setRows(5);
        messageScrollPane.setViewportView(messageTextArea);

        messagePanel.add(messageScrollPane);

        add(messagePanel, BorderLayout.CENTER);
    }
    
    public void showMail(EMail mail){
        readingMail = mail;
        
        actualSubjectLabel.setText(mail.getSubject());
        
        actualFromLabel.setText(mail.getSender().getName() + " <"+mail.getSender().getAddress()+">");
        
        String toStr = "";
        ArrayList<User> toUsers = mail.getReceivers();
        for(int i = 0; i < toUsers.size(); i++){
            toStr += toUsers.get(i).getName()+" <" +toUsers.get(i).getAddress() +">";
            if(i < toUsers.size() -1)
                toStr += ", ";
        }
        actualToLabel.setText(toStr);
        
        actualDateLabel.setText(mail.getDateString());
        
        messageTextArea.setText(mail.getBody());
    }
}
