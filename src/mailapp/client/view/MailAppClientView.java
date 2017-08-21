
package mailapp.client.view;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import mailapp.EMail;
import mailapp.client.connection.ConnectionManager;
import mailapp.client.connection.ConnectionManagerMessage;
import mailapp.server.MailServer;

/**
 *
 * @author pauty
 */
public class MailAppClientView extends JFrame{ 
    //switching panels 
    private UserSelectionPanel userSelectionPanel;
    private InboxPanel inboxPanel;
    private MailReaderPanel mailReaderPanel;
    private MailWriterPanel mailWriterPanel;
    private ConnectionManager connectionManager;
    
    //receives info form connection manager and updates GUI panels
    private class ConnectionManagerObserver implements Observer{

        @Override
        public void update(Observable o, Object o1) {
            ConnectionManagerMessage msg = (ConnectionManagerMessage)o1;
            
            if(msg.getType() == ConnectionManagerMessage.Type.MAIL_FOLDER_UPDATE){
                //check if updated folder was inbox, to show new mail notification
                if(msg.getUpdatedFolderName().equals(MailServer.INBOX_FOLDERNAME)){
                    List<EMail> newMails = connectionManager.getNewInboxMails();
                    if(!newMails.isEmpty())
                        showNewMailMessageDialog(newMails);
                }
                //we update te view list only if it is the currently displayed list
                if(msg.getUpdatedFolderName().equals(inboxPanel.getCurrentFolderName())){
                    inboxPanel.updateFolderMails();
                }
            }
            else if(msg.getType() == ConnectionManagerMessage.Type.DISCONNECT){
                userSelectionPanel.setErrorMessage("ERROR - Lost connection to server.");
                showUserSelectionPanel();
            }
        }
    }
    
    public MailAppClientView(ConnectionManager cm){
        //adds a new observer to the connection manager
        connectionManager = cm;
        connectionManager.addObserver(new ConnectionManagerObserver());
        
        //init GUI
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        getContentPane().setLayout(new CardLayout());
        
        //create user selection panel
        userSelectionPanel = new UserSelectionPanel(this);
        //create inbox panel
        inboxPanel = new InboxPanel(this);
        //create mail reader panel
        mailReaderPanel = new MailReaderPanel(this);
        //create mail writer panel
        mailWriterPanel = new MailWriterPanel(this);
        
        //add switching panels to card layout
        getContentPane().add(userSelectionPanel, "userSelection");
        getContentPane().add(inboxPanel, "inbox");
        getContentPane().add(mailReaderPanel, "mailReader");
        getContentPane().add(mailWriterPanel, "mailWriter");

        this.setSize(800, 500);
        this.setMinimumSize(new Dimension(500,400));
        this.setTitle("MailApp Client");
    }

    
    public void showInboxPanel(){
        inboxPanel.setUserLabel(connectionManager.getCurrentUser());
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"inbox");
    }
    
    public void showUserSelectionPanel(){
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"userSelection");
    }
    
    public void showMailReaderPanel(EMail mail){
        mailReaderPanel.showMail(mail);
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"mailReader");
        
    }
    
    public void showMailWriterPanel(EMail mail, EMail.Type t){
        mailWriterPanel.initFields(mail, t);
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"mailWriter");
    }   
    
    private void showNewMailMessageDialog(List<EMail> mails){
        String mailString = "";
        for(int i = 0; i < mails.size(); i++){
            mailString += mails.get(i).getSender().getName() + " - " + mails.get(i).getSubject() + "<br>";
        }
        //JOptionPane pane = new JOptionPane();
        //pane.cre
        /*JOptionPane.showMessageDialog(new JDialog(this),
                        "You have new Inbox mails:\n" + mailString,
                        "New Inbox Mails",
                         JOptionPane.INFORMATION_MESSAGE);
                         */
        JDialog dialog = new JDialog(this, "New Mails");
        dialog.setBounds(132, 132, 300, 200);
        JLabel label = new JLabel("<html>You have new inbox mails: <br><br>" + mailString + "</html>");
        //JLabel iconLabel = new JLabel();
        //Icon icon = UIManager.getIcon("FileChooser.newFolderIcon");
        //iconLabel.setIcon(icon);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JButton button = new JButton("OK");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();   
            }
        });
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(label, BorderLayout.CENTER);
        //contentPane.add(iconLabel, BorderLayout.WEST);
        JPanel p2 = new JPanel();
        p2.add(button);
        contentPane.add(p2, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setSize(250,150);
        dialog.setMinimumSize(new Dimension(250,150));
        dialog.setVisible(true);
    }
    
    //for swithing panels' controllers usage only
    ConnectionManager getConnectionManager(){
        return connectionManager;
    }
}
