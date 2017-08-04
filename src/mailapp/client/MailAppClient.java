
package mailapp.client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mailapp.EMail;
import mailapp.User;
import mailapp.client.view.*;

/**
 *
 * @author pauty
 */
public class MailAppClient extends JFrame{
    
    private final String USERNAME1 = "Alan Turing";
    private final String USERNAME2 = "John Von Neumann";
    private final String USERNAME3 = "Dijkstra";
    private final String USERADDRESS1 = "turing@mailapp.com";
    private final String USERADDRESS2 = "vonneumann@mailapp.com";
    private final String USERADDRESS3 = "dijkstra@mailapp.com";
    private static MailAppClient singleInstance = null;
    private JLabel jLabel1;
    
    private JComboBox<String> userComboBox;
    private JButton loginButton;
    
    //switching panels 
    private JPanel userSelectionPanel;
    
    private InboxPanel inboxPanel;
    private MailReaderPanel mailReaderPanel;
    //private MailWriterPanel mailWriterPanel;
    
    private class loginButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            String userName = (String)userComboBox.getSelectedItem();
            String userAddress = null;
            User user;
            
            if(userName.equals(USERNAME1)){
                userAddress = USERADDRESS1;
            }
            else if(userName.equals(USERNAME2)){
                userAddress = USERADDRESS2;
            }
            else if(userName.equals(USERNAME3)){
                userAddress = USERADDRESS3;
            }
            
            //we change te current user
            user = new User(userName, userAddress);
            inboxPanel.setUser(user); //GUI
            //future change in controller?
            
            showInboxPanel();
        }  
    }
    
    private MailAppClient(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new CardLayout());
        
        //create user selection panel
        userSelectionPanel = new JPanel();
        jLabel1 = new javax.swing.JLabel();
        userComboBox = new JComboBox<String>();
        loginButton = new JButton();

        jLabel1.setFont(new Font("Noto Sans", 0, 14)); 
        jLabel1.setText("Please select an user   ");
        userSelectionPanel.add(jLabel1);

        userComboBox.setFont(new Font("Noto Sans", 0, 14)); 
        userComboBox.setModel(new DefaultComboBoxModel<>(new String[] { USERNAME1, USERNAME2, USERNAME3 }));
        userSelectionPanel.add(userComboBox);
        
        loginButton.addActionListener(new loginButtonListener());
        loginButton.setText("Login");
        userSelectionPanel.add(loginButton);
        
        //create inbox panel
        inboxPanel = new InboxPanel();
        
        //create mail reader panel
        mailReaderPanel = new MailReaderPanel();
        
        //create mail writer panel
        
        //add switching panels to card layout
        getContentPane().add(userSelectionPanel, "userSelection");
        getContentPane().add(inboxPanel, "inbox");
        getContentPane().add(mailReaderPanel, "mailReader");
        
        
        
        this.setSize(800, 500);
    }
    
    public static MailAppClient getInstance(){
        if(singleInstance == null)
            singleInstance = new MailAppClient();
        return singleInstance;
    }
    
    public void showInboxPanel(){
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"inbox");
    }
    
    public void showUserSelectionPanel(){
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"userSelection");
    }
    
    public void showMailReaderPanel(EMail mail){
        mailReaderPanel.showMail(mail);
        ((CardLayout)this.getContentPane().getLayout()).show(this.getContentPane(),"mailReader");
        
    }
    
    public void showMailWriterPanel(EMail mail, boolean isReply){
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*
        //LOOK AND FEEL
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            
        } catch (InstantiationException ex) {
            
        } catch (IllegalAccessException ex) {
            
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            
        }
        */
        
        //qui creare i controller di invio/gestione e  ricezione
        MailAppClient.getInstance().setVisible(true);
    }
    
}
