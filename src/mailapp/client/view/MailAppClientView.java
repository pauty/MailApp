
package mailapp.client.view;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import mailapp.EMail;
import mailapp.client.connection.ConnectionManager;

/**
 *
 * @author pauty
 */
public class MailAppClientView extends JFrame{
    private static MailAppClientView singleInstance = null;
    
    //switching panels 
    private UserSelectionPanel userSelectionPanel;
    private InboxPanel inboxPanel;
    private MailReaderPanel mailReaderPanel;
    private MailWriterPanel mailWriterPanel;
    
    //receives info form connection manager and updates GUI panels
    private class ConnectionManagerObserver implements Observer{

        @Override
        public void update(Observable o, Object o1) {
            inboxPanel.updateInboxList(ConnectionManager.getInstance().getInboxMailList());  
        }  
    }
    
    private MailAppClientView(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        getContentPane().setLayout(new CardLayout());
        
        //create user selection panel
        userSelectionPanel = new UserSelectionPanel();
        //create inbox panel
        inboxPanel = new InboxPanel();
        //create mail reader panel
        mailReaderPanel = new MailReaderPanel();
        //create mail writer panel
        mailWriterPanel = new MailWriterPanel();
        
        //add switching panels to card layout
        getContentPane().add(userSelectionPanel, "userSelection");
        getContentPane().add(inboxPanel, "inbox");
        getContentPane().add(mailReaderPanel, "mailReader");
        getContentPane().add(mailWriterPanel, "mailWriter");

        this.setSize(800, 500);
        this.setMinimumSize(new Dimension(500,300));
        
        //adds a new observer to the connection manager
        ConnectionManager.getInstance().addObserver(new ConnectionManagerObserver());
    }
    
    public static MailAppClientView getInstance(){
        if(singleInstance == null)
            singleInstance = new MailAppClientView();
        return singleInstance;
    }
    
    public void showInboxPanel(){
        inboxPanel.setUserLabel(ConnectionManager.getInstance().getCurrentUser());
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"inbox");
    }
    
    public void showUserSelectionPanel(){
        ConnectionManager.getInstance().setCurrentUser(null);
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
}
