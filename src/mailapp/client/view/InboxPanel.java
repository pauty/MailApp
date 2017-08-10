/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailapp.client.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.border.EmptyBorder;
import mailapp.EMail;
import mailapp.User;
    
/**
 *
 * @author pauty
 */
public class InboxPanel extends JPanel{
    private JButton newMailButton;
    private JButton deleteMailButton;
    private JButton jButton3;
    private JLabel welcomeLabel;
    private JLabel jLabel3;
    private JList<EMail> mailList;
    private DefaultListModel<EMail> listModel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel buttonsPanel;
    private JScrollPane jScrollPane1;
    
    private class ButtonsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("New Mail")){
                MailAppClientView.getInstance().showMailWriterPanel(null, EMail.Type.NEW);
            }
            else if(ae.getActionCommand().equals("Delete")){
            }
            else{
                //SOULD NOT BE HERE
            }
        }
        
    }
    
    private int previousSelectedIndex = -2;
    
    private class ListListener extends MouseAdapter{  
        @Override
        public void mouseClicked(MouseEvent me) {
            if(me.isControlDown()){
                previousSelectedIndex = -2;
                return;
            }  
            int index = mailList.locationToIndex(me.getPoint());
            if(index == previousSelectedIndex){
                MailAppClientView.getInstance().showMailReaderPanel(listModel.get(index));
            }
            else{
                previousSelectedIndex = index;
            }
        }
   
    }
    
    public InboxPanel(){
        leftPanel = new JPanel();
        welcomeLabel = new JLabel();
        jLabel3 = new JLabel("");
        rightPanel = new JPanel();
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        newMailButton = new JButton("New Mail");
        deleteMailButton = new JButton("Delete");
        jButton3 = new JButton();
        jScrollPane1 = new JScrollPane();
        mailList = new JList<>();

        setLayout(new BorderLayout());
        
        leftPanel.add(welcomeLabel);

        leftPanel.add(jLabel3);

        add(leftPanel, java.awt.BorderLayout.LINE_START);

        rightPanel.setLayout(new java.awt.BorderLayout());

        buttonsPanel.add(newMailButton);
        buttonsPanel.add(deleteMailButton);

        jButton3.setText("jButton3");
        buttonsPanel.add(jButton3);

        rightPanel.add(buttonsPanel, BorderLayout.PAGE_START);
        
        ButtonsListener buttonListener = new ButtonsListener();
        newMailButton.addActionListener(buttonListener);
        deleteMailButton.addActionListener(buttonListener);
        
        
        EMail[] mailArray = new EMail[3];
        User u = new User("bubba","bubba@mail.com");
        User u2 = new User("zzz","turing@mailapp.com");
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(u);
        userList.add(u);
        userList.add(u2);
        mailArray[0] = new EMail(-1, u, userList, "testmail1", "body here eee", new Date(), 0 ,0);
        mailArray[1] = new EMail(-1, u, userList, "testmail2", "haha", new Date(), 0 ,0);
        mailArray[2] = new EMail(-1, u, userList, "testmail3", "body here qqq", new Date(), 0 ,0);
        
        listModel = new DefaultListModel<EMail>();
        
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        listModel.addElement(mailArray[0]);
        listModel.addElement(mailArray[1]);
        listModel.addElement(mailArray[2]);
        
        ListCellRenderer<EMail> r = (ListCellRenderer<EMail>)new InboxMailRenderer();
        mailList.setCellRenderer(r);
        mailList.addMouseListener(new ListListener());

        mailList.setModel(listModel);
        jScrollPane1.setViewportView(mailList);

        rightPanel.add(jScrollPane1, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);
        
        this.setBorder(new EmptyBorder(10,10,10,10));
        
    }
    
    public void setUserLabel(User u){
        welcomeLabel.setText("Welcome back, " + u.getName());
    }
    
    public void updateInboxList(ArrayList<EMail> mailList){
        listModel.clear();
        previousSelectedIndex = -2;
        for(int i = 0; i < mailList.size(); i++){
            listModel.addElement(mailList.get(i));
        }   
    }
}
