
package mailapp.client.view;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import mailapp.EMail;
import mailapp.client.connection.ConnectionManager;

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
            ConnectionManager.LastAction action = (ConnectionManager.LastAction)o1;
            if(action.equals(ConnectionManager.LastAction.INBOX_UPDATE)){
                List<EMail> newMails = connectionManager.getNewInboxMails();
                if(!newMails.isEmpty())
                    showNewMailMessageDialog(newMails);
                inboxPanel.updateFolderMails();  
            }
            else if(action.equals(ConnectionManager.LastAction.OTHER_UPDATE)){
                inboxPanel.updateFolderMails();
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
        this.setMinimumSize(new Dimension(500,300));
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
            mailString += mails.get(i).getSender().getName() + " - " + mails.get(i).getSubject() + "\n";
        }
        //JOptionPane pane = new JOptionPane();
        //pane.cre
        JOptionPane.showMessageDialog(new JDialog(this),
                        "You have new Inbox mails:\n" + mailString,
                        "New Inbox Mails",
                         JOptionPane.INFORMATION_MESSAGE);
    }
    
    //for swithing panels usage only
    ConnectionManager getConnectionManager(){
        return connectionManager;
    }
}
