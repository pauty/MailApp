
package mailapp.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import mailapp.EMail;
import mailapp.User;
import mailapp.client.connection.ConnectionManager;

public class MailWriterPanel extends JPanel {
    private JPanel buttonsPanel;
    private JButton cancelButton;
    private JPanel fieldsPanel;
    private JPanel formalLabelPanel;
    private JPanel messagePanel;
    private JScrollPane messageScrollPane;
    private JTextArea messageTextArea;
    private JButton sendButton;
    private JTextField subjectField;
    private JLabel subjectLabel;
    private JTextField toField;
    private JLabel toLabel;
    private JPanel topPanel;
    private MailAppClientView parentFrame;
    
    private int inReplyTo;
   
    //CONTROLLER
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Cancel")){
                Object[] options = {"Yes","No"};
                int res = JOptionPane.showOptionDialog(new JDialog(),
                    "You haven't send this mail.\nAre you sure you want to discard it?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]); //default button title
                if(res == JOptionPane.YES_OPTION){
                   parentFrame.showInboxPanel();
                }
            }
            else if(ae.getActionCommand().equals("Send")){
                String subject = subjectField.getText();
                String to = toField.getText();
                if(subject.isEmpty() || to.isEmpty()){
                    JOptionPane.showMessageDialog( new JDialog(),
                        "Please fill both subject and\nto fields.",
                        "Invalid Field",
                        JOptionPane.ERROR_MESSAGE);
                } 
                else{ 
                    String body = messageTextArea.getText();
                    parentFrame.getConnectionManager().sendMail(to, subject, body, inReplyTo);
                    parentFrame.showInboxPanel();
                }
            }
        }
    }
    
    //END CONTROLLER
    
    public MailWriterPanel(MailAppClientView parent){
        //set parent
        parentFrame = parent;
        
        //init GUI
        topPanel = new JPanel();
        formalLabelPanel = new JPanel();
        subjectLabel = new JLabel("Subject");
        toLabel = new JLabel("To");
        fieldsPanel = new JPanel();
        subjectField = new JTextField();
        toField = new JTextField();
        buttonsPanel = new JPanel();
        cancelButton = new JButton("Cancel");
        sendButton = new JButton("Send");
        messagePanel = new JPanel();
        messageScrollPane = new JScrollPane();
        messageTextArea = new JTextArea();

        setLayout(new BorderLayout());

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout());

        formalLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        formalLabelPanel.setLayout(new GridLayout(2, 1));

        subjectLabel.setFont(new Font("Noto Sans", 1, 14)); 
        formalLabelPanel.add(subjectLabel);

        toLabel.setFont(new Font("Noto Sans", 1, 14)); 
        formalLabelPanel.add(toLabel);

        topPanel.add(formalLabelPanel, BorderLayout.WEST);

        fieldsPanel.setLayout(new GridLayout(2, 1));

        subjectField.setFont(new Font("Noto Sans", 0, 14)); 
        fieldsPanel.add(subjectField);

        toField.setFont(new Font("Noto Sans", 0, 14));
        fieldsPanel.add(toField);

        topPanel.add(fieldsPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout());

        cancelButton.setFont(new Font("Noto Sans", 1, 16));
        buttonsPanel.add(cancelButton);

        sendButton.setFont(new Font("Noto Sans", 1, 16));
        buttonsPanel.add(sendButton);
        
        ActionListener listener = new ButtonsListener();
        cancelButton.addActionListener(listener);
        sendButton.addActionListener(listener);

        topPanel.add(buttonsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.PAGE_START);

        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));
        
        messageTextArea.setMargin(new Insets(20, 20, 20, 20));
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);

        messageScrollPane.setViewportView(messageTextArea);

        messagePanel.add(messageScrollPane);

        add(messagePanel, BorderLayout.CENTER);
    }
    
    public void initFields(EMail mail, EMail.Type t){
        //reset
        subjectField.setText("");
        toField.setText("");
        messageTextArea.setText("");
        messageTextArea.setCaretPosition(0);
        inReplyTo = -1;
        
        String[] bodyLines;
        
        switch(t){
            case NEW:
                //nothing to do
                break;
            case FORWARD:
                if(!mail.getSubject().contains("Fw: ")){
                    subjectField.setText("Fw: " + mail.getSubject());
                }
                messageTextArea.setText("\n\n----------------------------------------------------------------\n\n");
                messageTextArea.append("original message:\n\n");
                messageTextArea.append("Subject: " + mail.getSubject() + "\n");
                messageTextArea.append("From: " + mail.getSender().getAddress() + "\n");
                messageTextArea.append("To: " + User.printUserAddressesList(mail.getReceivers(), ", ") + "\n");
                messageTextArea.append("Date: " + mail.getDateString("dd/MM/yyyy   HH:mm:ss") + "\n");
                bodyLines = mail.getBody().split("\n");
                for(int i = 0; i < bodyLines.length; i++){
                    messageTextArea.append("|   " + bodyLines[i] + "\n");
                }
                //messageTextArea.append("\n" + mail.getBody());
                break;
            case REPLY:
            case REPLY_ALL:
                inReplyTo = mail.getID();
                //set subject
                if(!mail.getSubject().contains("Re: ")){
                    subjectField.setText("Re: " + mail.getSubject());
                }
                else{
                    subjectField.setText(mail.getSubject());
                }
                //set to
                toField.setText(mail.getSender().getAddress());
                if(t == EMail.Type.REPLY_ALL){
                    ArrayList<User> userList= new ArrayList<User>(mail.getReceivers());
                    userList.remove(parentFrame.getConnectionManager().getCurrentUser());
                    toField.setText(toField.getText() + ", " + User.printUserAddressesList(userList, ", "));
                }
                //show previous message
                messageTextArea.setText("\n\n----------------------------------------------------------------\n\n");
                messageTextArea.append("in date " + mail.getDateString("dd/MM/yyyy HH:mm:ss") +" "+mail.getSender().getAddress()+" wrote:\n\n");
                bodyLines = mail.getBody().split("\n");
                for(int i = 0; i < bodyLines.length; i++){
                    messageTextArea.append("|   " + bodyLines[i] + "\n");
                }
                break;   
        }
    }            
}